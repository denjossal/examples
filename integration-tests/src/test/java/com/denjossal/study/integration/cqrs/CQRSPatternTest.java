package com.denjossal.study.integration.cqrs;

import static org.assertj.core.api.Assertions.*;

import java.sql.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import redis.clients.jedis.Jedis;

/**
 * CQRS (Command Query Responsibility Segregation) with PostgreSQL + Redis.
 *
 * Write side: PostgreSQL (source of truth, normalized, transactional)
 * Read side: Redis (denormalized, fast, eventually consistent)
 *
 * Flow:
 *   1. Command (write) → PostgreSQL
 *   2. Project change to Redis read model (sync or via events)
 *   3. Query (read) → Redis (fast, pre-computed)
 *
 * Benefits: optimize read and write independently, scale reads via caching.
 * Trade-off: eventual consistency between write and read models.
 */
@Testcontainers
class CQRSPatternTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("cqrs_db");

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    private Connection writeDb;
    private Jedis readStore;

    @BeforeEach
    void setUp() throws SQLException {
        writeDb = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        try (var stmt = writeDb.createStatement()) {
            stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS products (
                        id VARCHAR(50) PRIMARY KEY,
                        name VARCHAR(200) NOT NULL,
                        price DECIMAL(10,2) NOT NULL,
                        stock INTEGER NOT NULL DEFAULT 0
                    )
                    """);
            stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS order_items (
                        id SERIAL PRIMARY KEY,
                        order_id VARCHAR(50) NOT NULL,
                        product_id VARCHAR(50) NOT NULL,
                        quantity INTEGER NOT NULL,
                        unit_price DECIMAL(10,2) NOT NULL
                    )
                    """);
        }

        readStore = new Jedis(redis.getHost(), redis.getMappedPort(6379));
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (var stmt = writeDb.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS order_items");
            stmt.execute("DROP TABLE IF EXISTS products");
        }
        writeDb.close();
        readStore.close();
    }

    @Test
    void shouldWriteToPostgresAndProjectToRedis() throws SQLException {
        // COMMAND: Create product (write side)
        createProduct("PROD-1", "Widget", 29.99, 100);

        // PROJECT: Update read model in Redis
        projectProductToReadModel("PROD-1");

        // QUERY: Read from Redis (fast, denormalized)
        var product = readStore.hgetAll("product:PROD-1");

        assertThat(product.get("name")).isEqualTo("Widget");
        assertThat(product.get("price")).isEqualTo("29.99");
        assertThat(product.get("stock")).isEqualTo("100");
    }

    @Test
    void shouldMaintainLeaderboard() throws SQLException {
        // Setup products
        createProduct("P1", "Laptop", 999.99, 50);
        createProduct("P2", "Phone", 699.99, 200);
        createProduct("P3", "Tablet", 399.99, 150);

        // Place orders (write side)
        placeOrder("ORD-1", "P2", 5, 699.99);
        placeOrder("ORD-2", "P1", 2, 999.99);
        placeOrder("ORD-3", "P2", 3, 699.99);
        placeOrder("ORD-4", "P3", 1, 399.99);

        // Project to Redis sorted set (read model: top products by revenue)
        projectRevenueLeaderboard();

        // QUERY: Top products by revenue
        var topProducts = readStore.zrevrangeWithScores("leaderboard:revenue", 0, 2);

        assertThat(topProducts).hasSize(3);
        assertThat(topProducts.get(0).getElement()).isEqualTo("P2"); // 8 * 699.99
        assertThat(topProducts.get(1).getElement()).isEqualTo("P1"); // 2 * 999.99
    }

    @Test
    void shouldInvalidateReadModelOnUpdate() throws SQLException {
        createProduct("PROD-X", "OldName", 10.00, 50);
        projectProductToReadModel("PROD-X");

        assertThat(readStore.hget("product:PROD-X", "name")).isEqualTo("OldName");

        // COMMAND: Update product (write side)
        try (var ps = writeDb.prepareStatement("UPDATE products SET name = ?, price = ? WHERE id = ?")) {
            ps.setString(1, "NewName");
            ps.setDouble(2, 15.00);
            ps.setString(3, "PROD-X");
            ps.executeUpdate();
        }

        // Re-project (in real systems: triggered by event/CDC)
        projectProductToReadModel("PROD-X");

        assertThat(readStore.hget("product:PROD-X", "name")).isEqualTo("NewName");
        assertThat(readStore.hget("product:PROD-X", "price")).isEqualTo("15.0");
    }

    // ─── Write side (Commands) ──────────────────────────────────────────────

    private void createProduct(String id, String name, double price, int stock) throws SQLException {
        try (var ps = writeDb.prepareStatement("INSERT INTO products (id, name, price, stock) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            ps.executeUpdate();
        }
    }

    private void placeOrder(String orderId, String productId, int qty, double price) throws SQLException {
        try (var ps = writeDb.prepareStatement(
                "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, orderId);
            ps.setString(2, productId);
            ps.setInt(3, qty);
            ps.setDouble(4, price);
            ps.executeUpdate();
        }
    }

    // ─── Projection (Write → Read model) ────────────────────────────────────

    private void projectProductToReadModel(String productId) throws SQLException {
        try (var ps = writeDb.prepareStatement("SELECT * FROM products WHERE id = ?")) {
            ps.setString(1, productId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                readStore.hset(
                        "product:" + productId,
                        Map.of(
                                "name", rs.getString("name"),
                                "price", String.valueOf(rs.getDouble("price")),
                                "stock", String.valueOf(rs.getInt("stock"))));
            }
        }
    }

    private void projectRevenueLeaderboard() throws SQLException {
        try (var stmt = writeDb.createStatement()) {
            var rs = stmt.executeQuery(
                    """
                    SELECT product_id, SUM(quantity * unit_price) as revenue
                    FROM order_items
                    GROUP BY product_id
                    """);
            while (rs.next()) {
                readStore.zadd("leaderboard:revenue", rs.getDouble("revenue"), rs.getString("product_id"));
            }
        }
    }
}
