package com.denjossal.study.integration.distributed;

import static org.assertj.core.api.Assertions.*;

import java.sql.*;
import java.time.Duration;
import java.util.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.kafka.KafkaContainer;

/**
 * Transactional Outbox + Poller — guaranteed at-least-once event delivery.
 *
 * This is the REAL implementation:
 * 1. Application writes business data + outbox entry in ONE transaction (PostgreSQL)
 * 2. Poller reads unpublished outbox entries
 * 3. Publishes each to Kafka
 * 4. Marks as published in the same step
 *
 * This guarantees no lost events (unlike direct Kafka publish which can fail
 * after DB commit). Trade-off: slight delay (polling interval).
 *
 * In production: use Debezium CDC instead of polling for lower latency.
 */
@Testcontainers
class OutboxPollerTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("outbox_db");

    @Container
    static final KafkaContainer kafka = new KafkaContainer("apache/kafka:3.8.0");

    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        try (var stmt = conn.createStatement()) {
            stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS orders (
                        id VARCHAR(50) PRIMARY KEY,
                        customer_id VARCHAR(50),
                        total DECIMAL(10,2),
                        status VARCHAR(20)
                    )
                    """);
            stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS outbox (
                        id SERIAL PRIMARY KEY,
                        aggregate_id VARCHAR(50) NOT NULL,
                        event_type VARCHAR(100) NOT NULL,
                        payload TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        published BOOLEAN DEFAULT FALSE
                    )
                    """);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS outbox");
            stmt.execute("DROP TABLE IF EXISTS orders");
        }
        conn.close();
    }

    @Test
    void shouldWriteOrderAndOutboxAtomically() throws SQLException {
        conn.setAutoCommit(false);

        String orderId = "ORD-100";
        try (var ps =
                conn.prepareStatement("INSERT INTO orders (id, customer_id, total, status) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, orderId);
            ps.setString(2, "CUST-1");
            ps.setDouble(3, 199.99);
            ps.setString(4, "PLACED");
            ps.executeUpdate();
        }

        try (var ps =
                conn.prepareStatement("INSERT INTO outbox (aggregate_id, event_type, payload) VALUES (?, ?, ?)")) {
            ps.setString(1, orderId);
            ps.setString(2, "OrderPlaced");
            ps.setString(3, """
                    {"orderId":"ORD-100","customerId":"CUST-1","total":199.99}""");
            ps.executeUpdate();
        }

        conn.commit();
        conn.setAutoCommit(true);

        // Both in same transaction — verify
        try (var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM orders");
            rs.next();
            assertThat(rs.getInt(1)).isEqualTo(1);

            rs = stmt.executeQuery("SELECT COUNT(*) FROM outbox WHERE published = FALSE");
            rs.next();
            assertThat(rs.getInt(1)).isEqualTo(1);
        }
    }

    @Test
    void shouldPollOutboxAndPublishToKafka() throws Exception {
        // Write 3 events to outbox
        for (int i = 1; i <= 3; i++) {
            writeToOutbox(
                    "ORD-" + i,
                    "OrderPlaced",
                    """
                    {"orderId":"ORD-%d","total":%d}""".formatted(i, i * 100));
        }

        // Run the poller: read unpublished → publish to Kafka → mark published
        int published = pollAndPublish("outbox-events");

        assertThat(published).isEqualTo(3);

        // Verify Kafka received all events
        var kafkaMessages = consumeAll("outbox-events", 3);
        assertThat(kafkaMessages).hasSize(3);
        assertThat(kafkaMessages.get(0)).contains("ORD-1");

        // Verify outbox entries marked as published
        try (var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM outbox WHERE published = TRUE");
            rs.next();
            assertThat(rs.getInt(1)).isEqualTo(3);
        }
    }

    @Test
    void shouldBeIdempotentOnRepoll() throws Exception {
        writeToOutbox("ORD-X", "OrderPlaced", """
                {"orderId":"ORD-X"}""");

        pollAndPublish("outbox-idempotent");
        int secondRun = pollAndPublish("outbox-idempotent");

        assertThat(secondRun).isEqualTo(0);
    }

    // ─── Implementation ─────────────────────────────────────────────────────

    private void writeToOutbox(String aggregateId, String eventType, String payload) throws SQLException {
        try (var ps =
                conn.prepareStatement("INSERT INTO outbox (aggregate_id, event_type, payload) VALUES (?, ?, ?)")) {
            ps.setString(1, aggregateId);
            ps.setString(2, eventType);
            ps.setString(3, payload);
            ps.executeUpdate();
        }
    }

    private int pollAndPublish(String topic) throws Exception {
        int count = 0;
        try (var ps = conn.prepareStatement(
                "SELECT id, aggregate_id, payload FROM outbox WHERE published = FALSE ORDER BY id")) {
            var rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String key = rs.getString("aggregate_id");
                String payload = rs.getString("payload");

                // Publish to Kafka
                try (var producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(Map.of(
                        "bootstrap.servers", kafka.getBootstrapServers(),
                        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
                        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer"))) {
                    producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>(topic, key, payload))
                            .get();
                }

                // Mark published
                try (var update = conn.prepareStatement("UPDATE outbox SET published = TRUE WHERE id = ?")) {
                    update.setInt(1, id);
                    update.executeUpdate();
                }
                count++;
            }
        }
        return count;
    }

    private List<String> consumeAll(String topic, int expected) {
        try (var consumer = new KafkaConsumer<String, String>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "outbox-verify-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
            consumer.subscribe(List.of(topic));
            var results = new ArrayList<String>();
            long deadline = System.currentTimeMillis() + 10_000;
            while (results.size() < expected && System.currentTimeMillis() < deadline) {
                consumer.poll(Duration.ofMillis(500)).forEach(r -> results.add(r.value()));
            }
            return results;
        }
    }
}
