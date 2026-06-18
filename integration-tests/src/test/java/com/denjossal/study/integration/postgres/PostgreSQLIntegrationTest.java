package com.denjossal.study.integration.postgres;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

import java.sql.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Real PostgreSQL integration test using Testcontainers.
 * Demonstrates: schema creation, CRUD, transactions, and SQL patterns.
 */
@Testcontainers
class PostgreSQLIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        try (var stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id SERIAL PRIMARY KEY,
                        user_id INTEGER REFERENCES users(id),
                        total DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING'
                    )
                    """);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS orders");
            stmt.execute("DROP TABLE IF EXISTS users");
        }
        conn.close();
    }

    @Test
    void shouldInsertAndQueryUser() throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT INTO users (name, email) VALUES (?, ?) RETURNING id")) {
            ps.setString(1, "Alice");
            ps.setString(2, "alice@test.com");
            var rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            int id = rs.getInt("id");
            assertThat(id).isGreaterThan(0);
        }

        try (var ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            ps.setString(1, "alice@test.com");
            var rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Alice");
        }
    }

    @Test
    void shouldEnforceUniqueConstraint() throws SQLException {
        try (var ps = conn.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)")) {
            ps.setString(1, "Bob");
            ps.setString(2, "bob@test.com");
            ps.executeUpdate();
        }

        assertThatThrownBy(() -> {
            try (var ps = conn.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)")) {
                ps.setString(1, "Bob2");
                ps.setString(2, "bob@test.com"); // duplicate email
                ps.executeUpdate();
            }
        }).isInstanceOf(SQLException.class);
    }

    @Test
    void shouldJoinUsersAndOrders() throws SQLException {
        // Insert user
        int userId;
        try (var ps = conn.prepareStatement(
                "INSERT INTO users (name, email) VALUES (?, ?) RETURNING id")) {
            ps.setString(1, "Carol");
            ps.setString(2, "carol@test.com");
            var rs = ps.executeQuery();
            rs.next();
            userId = rs.getInt("id");
        }

        // Insert orders
        try (var ps = conn.prepareStatement(
                "INSERT INTO orders (user_id, total, status) VALUES (?, ?, ?)")) {
            ps.setInt(1, userId);
            ps.setDouble(2, 99.99);
            ps.setString(3, "SHIPPED");
            ps.executeUpdate();

            ps.setInt(1, userId);
            ps.setDouble(2, 250.00);
            ps.setString(3, "PLACED");
            ps.executeUpdate();
        }

        // Join query
        try (var ps = conn.prepareStatement("""
                SELECT u.name, o.total, o.status
                FROM users u JOIN orders o ON u.id = o.user_id
                WHERE u.id = ?
                ORDER BY o.total DESC
                """)) {
            ps.setInt(1, userId);
            var rs = ps.executeQuery();

            var orders = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                orders.add(Map.of(
                        "name", rs.getString("name"),
                        "total", rs.getDouble("total"),
                        "status", rs.getString("status")
                ));
            }

            assertThat(orders).hasSize(2);
            assertThat(orders.get(0).get("total")).isEqualTo(250.00);
        }
    }

    @Test
    void shouldRollbackTransaction() throws SQLException {
        conn.setAutoCommit(false);

        try (var ps = conn.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)")) {
            ps.setString(1, "Rollback");
            ps.setString(2, "rollback@test.com");
            ps.executeUpdate();
        }

        conn.rollback();
        conn.setAutoCommit(true);

        try (var ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")) {
            ps.setString(1, "rollback@test.com");
            var rs = ps.executeQuery();
            rs.next();
            assertThat(rs.getInt(1)).isEqualTo(0);
        }
    }
}
