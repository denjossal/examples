package com.denjossal.study.integration.saga;

import static org.assertj.core.api.Assertions.*;

import java.sql.*;
import java.time.Duration;
import java.util.*;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.kafka.KafkaContainer;

/**
 * Saga Pattern — distributed transaction across services via events.
 *
 * Scenario: Order placement saga
 *   1. OrderService creates order (PENDING)
 *   2. Publishes "order.created" event to Kafka
 *   3. InventoryService reserves stock → publishes "inventory.reserved"
 *   4. PaymentService charges → publishes "payment.completed"
 *   5. OrderService marks order CONFIRMED
 *
 * Compensation (on failure):
 *   - If payment fails → publish "payment.failed"
 *   - InventoryService releases stock (compensating transaction)
 *   - OrderService marks order CANCELLED
 *
 * This test proves the pattern works end-to-end with real Kafka + PostgreSQL.
 */
@Testcontainers
class SagaPatternTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer("apache/kafka:3.8.0");

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("saga_db");

    private Connection conn;

    @BeforeAll
    static void createTopics() throws Exception {
        try (var admin =
                AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
            admin.createTopics(List.of(
                            new NewTopic("order.created", 1, (short) 1),
                            new NewTopic("inventory.reserved", 1, (short) 1),
                            new NewTopic("inventory.released", 1, (short) 1),
                            new NewTopic("payment.completed", 1, (short) 1),
                            new NewTopic("payment.failed", 1, (short) 1)))
                    .all()
                    .get();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        try (var stmt = conn.createStatement()) {
            stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS orders (
                        id VARCHAR(50) PRIMARY KEY,
                        customer_id VARCHAR(50) NOT NULL,
                        total DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                    )
                    """);
            stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS inventory (
                        product_id VARCHAR(50) PRIMARY KEY,
                        quantity INTEGER NOT NULL,
                        reserved INTEGER NOT NULL DEFAULT 0
                    )
                    """);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS orders");
            stmt.execute("DROP TABLE IF EXISTS inventory");
        }
        conn.close();
    }

    @Test
    void shouldCompleteSagaSuccessfully() throws Exception {
        // Setup: stock available
        insertInventory("PROD-1", 10);

        // Step 1: Create order
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        createOrder(orderId, "CUST-1", 99.99);
        assertOrderStatus(orderId, "PENDING");

        // Step 2: Publish order.created event
        publishEvent(
                "order.created",
                orderId,
                """
                {"orderId":"%s","productId":"PROD-1","quantity":2,"total":99.99}
                """
                        .formatted(orderId));

        // Step 3: Inventory service processes → reserves stock
        String event = consumeEvent("order.created", "inventory-service");
        assertThat(event).contains(orderId);
        reserveInventory("PROD-1", 2);
        publishEvent(
                "inventory.reserved",
                orderId,
                """
                {"orderId":"%s","productId":"PROD-1","quantity":2}
                """
                        .formatted(orderId));

        // Step 4: Payment service processes → charges
        consumeEvent("inventory.reserved", "payment-service");
        publishEvent(
                "payment.completed",
                orderId,
                """
                {"orderId":"%s","amount":99.99}
                """.formatted(orderId));

        // Step 5: Order service confirms
        consumeEvent("payment.completed", "order-service");
        updateOrderStatus(orderId, "CONFIRMED");

        // Verify final state
        assertOrderStatus(orderId, "CONFIRMED");
        assertInventoryReserved("PROD-1", 2);
    }

    @Test
    void shouldCompensateOnPaymentFailure() throws Exception {
        // Setup
        insertInventory("PROD-2", 5);

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        createOrder(orderId, "CUST-2", 500.00);

        // Order created → inventory reserved
        publishEvent(
                "order.created",
                orderId,
                """
                {"orderId":"%s","productId":"PROD-2","quantity":1}
                """
                        .formatted(orderId));
        consumeEvent("order.created", "inventory-svc-comp");
        reserveInventory("PROD-2", 1);
        publishEvent(
                "inventory.reserved",
                orderId,
                """
                {"orderId":"%s","productId":"PROD-2","quantity":1}
                """
                        .formatted(orderId));

        // Payment FAILS
        consumeEvent("inventory.reserved", "payment-svc-comp");
        publishEvent(
                "payment.failed",
                orderId,
                """
                {"orderId":"%s","reason":"insufficient_funds"}
                """
                        .formatted(orderId));

        // Compensation: release inventory
        consumeEvent("payment.failed", "inventory-comp");
        releaseInventory("PROD-2", 1);
        publishEvent(
                "inventory.released",
                orderId,
                """
                {"orderId":"%s","productId":"PROD-2","quantity":1}
                """
                        .formatted(orderId));

        // Compensation: cancel order
        consumeEvent("payment.failed", "order-comp");
        updateOrderStatus(orderId, "CANCELLED");

        // Verify compensation
        assertOrderStatus(orderId, "CANCELLED");
        assertInventoryReserved("PROD-2", 0);
    }

    // ─── Helper methods (simulating service actions) ────────────────────────

    private void createOrder(String id, String customerId, double total) throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT INTO orders (id, customer_id, total, status) VALUES (?, ?, ?, 'PENDING')")) {
            ps.setString(1, id);
            ps.setString(2, customerId);
            ps.setDouble(3, total);
            ps.executeUpdate();
        }
    }

    private void updateOrderStatus(String orderId, String status) throws SQLException {
        try (var ps = conn.prepareStatement("UPDATE orders SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setString(2, orderId);
            ps.executeUpdate();
        }
    }

    private void insertInventory(String productId, int quantity) throws SQLException {
        try (var ps =
                conn.prepareStatement("INSERT INTO inventory (product_id, quantity, reserved) VALUES (?, ?, 0)")) {
            ps.setString(1, productId);
            ps.setInt(2, quantity);
            ps.executeUpdate();
        }
    }

    private void reserveInventory(String productId, int qty) throws SQLException {
        try (var ps = conn.prepareStatement(
                "UPDATE inventory SET reserved = reserved + ? WHERE product_id = ? AND quantity - reserved >= ?")) {
            ps.setInt(1, qty);
            ps.setString(2, productId);
            ps.setInt(3, qty);
            int updated = ps.executeUpdate();
            assertThat(updated).isEqualTo(1);
        }
    }

    private void releaseInventory(String productId, int qty) throws SQLException {
        try (var ps = conn.prepareStatement("UPDATE inventory SET reserved = reserved - ? WHERE product_id = ?")) {
            ps.setInt(1, qty);
            ps.setString(2, productId);
            ps.executeUpdate();
        }
    }

    private void assertOrderStatus(String orderId, String expected) throws SQLException {
        try (var ps = conn.prepareStatement("SELECT status FROM orders WHERE id = ?")) {
            ps.setString(1, orderId);
            var rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("status")).isEqualTo(expected);
        }
    }

    private void assertInventoryReserved(String productId, int expected) throws SQLException {
        try (var ps = conn.prepareStatement("SELECT reserved FROM inventory WHERE product_id = ?")) {
            ps.setString(1, productId);
            var rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("reserved")).isEqualTo(expected);
        }
    }

    private void publishEvent(String topic, String key, String value) throws Exception {
        try (var producer = new KafkaProducer<String, String>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()))) {
            producer.send(new ProducerRecord<>(topic, key, value)).get();
        }
    }

    private String consumeEvent(String topic, String groupId) {
        try (var consumer = new KafkaConsumer<String, String>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
            consumer.subscribe(List.of(topic));
            long deadline = System.currentTimeMillis() + 10_000;
            while (System.currentTimeMillis() < deadline) {
                var records = consumer.poll(Duration.ofMillis(500));
                if (!records.isEmpty()) {
                    return records.iterator().next().value();
                }
            }
        }
        throw new RuntimeException("No event received on topic: " + topic);
    }
}
