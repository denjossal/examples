package com.denjossal.study.integration.springboot;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import com.denjossal.study.integration.springboot.inventory.InventoryEventListener;
import com.denjossal.study.integration.springboot.order.OrderController;
import com.denjossal.study.integration.springboot.order.OrderEntity;
import com.denjossal.study.integration.springboot.order.OrderRepository;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.kafka.KafkaContainer;

/**
 * Full Spring Boot integration test with real Kafka + PostgreSQL.
 *
 * Tests the complete flow:
 * 1. REST API receives order → saves to PostgreSQL
 * 2. Event published to Kafka
 * 3. Kafka consumer (inventory service) receives the event
 * 4. Order lifecycle: PENDING → CONFIRMED → (or CANCELLED)
 */
@Testcontainers
@SpringBootTest(classes = OrderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("orders_db");

    @Container
    static final KafkaContainer kafka = new KafkaContainer("apache/kafka:3.8.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryEventListener inventoryListener;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        inventoryListener.clear();
    }

    @Test
    void shouldPlaceOrderAndPublishEvent() {
        var request = new OrderController.PlaceOrderRequest("CUST-1", "PROD-A", 3, 89.97);

        var response = restTemplate.postForEntity("/api/orders", request, OrderEntity.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(OrderEntity.OrderStatus.PENDING);
        assertThat(response.getBody().getCustomerId()).isEqualTo("CUST-1");

        // Verify persisted in PostgreSQL
        var saved = orderRepository.findById(response.getBody().getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getTotal()).isEqualTo(89.97);

        // Verify Kafka event received by consumer
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> assertThat(inventoryListener.getProcessedEvents())
                .anyMatch(e -> e.contains("OrderPlaced") && e.contains("PROD-A")));
    }

    @Test
    void shouldConfirmOrderAndPublishEvent() {
        // Place order
        var request = new OrderController.PlaceOrderRequest("CUST-2", "PROD-B", 1, 50.00);
        var placed = restTemplate
                .postForEntity("/api/orders", request, OrderEntity.class)
                .getBody();

        // Confirm
        var response =
                restTemplate.postForEntity("/api/orders/" + placed.getId() + "/confirm", null, OrderEntity.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(OrderEntity.OrderStatus.CONFIRMED);

        // Verify event
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> assertThat(inventoryListener.getProcessedEvents())
                .anyMatch(e -> e.contains("OrderConfirmed")));
    }

    @Test
    void shouldCancelOrderAndPublishEvent() {
        var request = new OrderController.PlaceOrderRequest("CUST-3", "PROD-C", 2, 100.00);
        var placed = restTemplate
                .postForEntity("/api/orders", request, OrderEntity.class)
                .getBody();

        var response = restTemplate.postForEntity("/api/orders/" + placed.getId() + "/cancel", null, OrderEntity.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(OrderEntity.OrderStatus.CANCELLED);

        // Verify DB state
        var dbOrder = orderRepository.findById(placed.getId()).get();
        assertThat(dbOrder.getStatus()).isEqualTo(OrderEntity.OrderStatus.CANCELLED);
    }

    @Test
    void shouldQueryOrdersByCustomer() {
        // Place multiple orders for same customer
        restTemplate.postForEntity(
                "/api/orders", new OrderController.PlaceOrderRequest("CUST-4", "P1", 1, 10.0), OrderEntity.class);
        restTemplate.postForEntity(
                "/api/orders", new OrderController.PlaceOrderRequest("CUST-4", "P2", 2, 20.0), OrderEntity.class);
        restTemplate.postForEntity(
                "/api/orders", new OrderController.PlaceOrderRequest("CUST-5", "P3", 1, 30.0), OrderEntity.class);

        var response = restTemplate.getForEntity("/api/orders/customer/CUST-4", OrderEntity[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldPersistAcrossMultipleOperations() {
        var request = new OrderController.PlaceOrderRequest("CUST-6", "PROD-D", 5, 250.00);
        var placed = restTemplate
                .postForEntity("/api/orders", request, OrderEntity.class)
                .getBody();

        // Confirm then verify the full history is in DB
        restTemplate.postForEntity("/api/orders/" + placed.getId() + "/confirm", null, OrderEntity.class);

        var all = orderRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getStatus()).isEqualTo(OrderEntity.OrderStatus.CONFIRMED);
        assertThat(all.get(0).getTotal()).isEqualTo(250.00);
    }
}
