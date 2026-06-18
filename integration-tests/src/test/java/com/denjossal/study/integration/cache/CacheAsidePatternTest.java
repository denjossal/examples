package com.denjossal.study.integration.cache;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import redis.clients.jedis.Jedis;

import java.sql.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Cache-Aside (Lazy Loading) pattern with Redis + PostgreSQL.
 *
 * Flow:
 *   1. Read: check cache first → hit → return cached value
 *   2. Read: cache miss → query DB → store in cache → return
 *   3. Write: update DB → invalidate cache (or write-through)
 *
 * This is the most common caching pattern. Trade-offs:
 * - Cache miss = extra latency (DB + cache write)
 * - Stale data possible if write invalidation fails
 * - Cache stampede on cold start (mitigate with warming or locking)
 */
@Testcontainers
class CacheAsidePatternTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cache_db");

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    private Connection db;
    private Jedis cache;
    private int dbQueryCount;

    @BeforeEach
    void setUp() throws SQLException {
        db = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        cache = new Jedis(redis.getHost(), redis.getMappedPort(6379));
        dbQueryCount = 0;

        try (var stmt = db.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id VARCHAR(50) PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(255) NOT NULL
                    )
                    """);
            stmt.execute("INSERT INTO users VALUES ('u1', 'Alice', 'alice@test.com')");
            stmt.execute("INSERT INTO users VALUES ('u2', 'Bob', 'bob@test.com')");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (var stmt = db.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
        }
        cache.flushAll();
        db.close();
        cache.close();
    }

    @Test
    void shouldReturnFromCacheOnHit() throws SQLException {
        // First call: cache miss → queries DB
        String result1 = getUser("u1");
        assertThat(result1).isEqualTo("Alice:alice@test.com");
        assertThat(dbQueryCount).isEqualTo(1);

        // Second call: cache hit → no DB query
        String result2 = getUser("u1");
        assertThat(result2).isEqualTo("Alice:alice@test.com");
        assertThat(dbQueryCount).isEqualTo(1); // still 1, no new DB call
    }

    @Test
    void shouldQueryDbOnCacheMiss() throws SQLException {
        assertThat(cache.exists("user:u2")).isFalse();

        String result = getUser("u2");

        assertThat(result).isEqualTo("Bob:bob@test.com");
        assertThat(cache.exists("user:u2")).isTrue();
        assertThat(dbQueryCount).isEqualTo(1);
    }

    @Test
    void shouldInvalidateCacheOnWrite() throws SQLException {
        // Populate cache
        getUser("u1");
        assertThat(cache.exists("user:u1")).isTrue();

        // Update DB + invalidate cache
        updateUser("u1", "Alice Updated", "alice-new@test.com");

        // Cache should be gone
        assertThat(cache.exists("user:u1")).isFalse();

        // Next read fetches fresh from DB
        String result = getUser("u1");
        assertThat(result).isEqualTo("Alice Updated:alice-new@test.com");
    }

    @Test
    void shouldSetTTLOnCacheEntry() throws SQLException {
        getUser("u1");

        long ttl = cache.ttl("user:u1");
        assertThat(ttl).isGreaterThan(0); // TTL was set
        assertThat(ttl).isLessThanOrEqualTo(300); // 5 minutes
    }

    @Test
    void shouldHandleWriteThrough() throws SQLException {
        // Write-through: update DB AND cache in one operation
        writeThroughUpdate("u2", "Bob Updated", "bob-new@test.com");

        // Cache has fresh data immediately (no invalidation needed)
        assertThat(cache.hget("user:u2", "name")).isEqualTo("Bob Updated");

        // DB also updated
        String fromDb = getUser("u2");
        assertThat(fromDb).isEqualTo("Bob Updated:bob-new@test.com");
    }

    // ─── Cache-Aside implementation ─────────────────────────────────────────

    private String getUser(String userId) throws SQLException {
        String cacheKey = "user:" + userId;

        // Check cache first
        if (cache.exists(cacheKey)) {
            var data = cache.hgetAll(cacheKey);
            return data.get("name") + ":" + data.get("email");
        }

        // Cache miss → query DB
        dbQueryCount++;
        try (var ps = db.prepareStatement("SELECT name, email FROM users WHERE id = ?")) {
            ps.setString(1, userId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");

                // Store in cache with TTL
                cache.hset(cacheKey, "name", name);
                cache.hset(cacheKey, "email", email);
                cache.expire(cacheKey, 300); // 5 min TTL

                return name + ":" + email;
            }
        }
        return null;
    }

    private void updateUser(String userId, String name, String email) throws SQLException {
        // Update DB
        try (var ps = db.prepareStatement("UPDATE users SET name = ?, email = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, userId);
            ps.executeUpdate();
        }
        // Invalidate cache
        cache.del("user:" + userId);
    }

    private void writeThroughUpdate(String userId, String name, String email) throws SQLException {
        // Update DB
        try (var ps = db.prepareStatement("UPDATE users SET name = ?, email = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, userId);
            ps.executeUpdate();
        }
        // Write-through: update cache with fresh data
        String cacheKey = "user:" + userId;
        cache.hset(cacheKey, "name", name);
        cache.hset(cacheKey, "email", email);
        cache.expire(cacheKey, 300);
    }
}
