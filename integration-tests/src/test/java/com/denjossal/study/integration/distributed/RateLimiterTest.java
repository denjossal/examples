package com.denjossal.study.integration.distributed;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.*;
import redis.clients.jedis.Jedis;

/**
 * Distributed Rate Limiter with Redis — Sliding Window and Token Bucket.
 *
 * Patterns:
 * 1. Fixed Window: simple counter per time window (INCR + EXPIRE)
 * 2. Sliding Window Log: sorted set of timestamps (more accurate)
 * 3. Token Bucket: tokens refill over time, consumed on request
 *
 * Use cases: API rate limiting, DDoS protection, fair resource sharing.
 * In production: use Redis + Lua for atomicity, or API Gateway rate limiting.
 */
@Testcontainers
class RateLimiterTest {

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    private Jedis jedis;

    @BeforeEach
    void setUp() {
        jedis = new Jedis(redis.getHost(), redis.getMappedPort(6379));
        jedis.flushAll();
    }

    @AfterEach
    void tearDown() {
        jedis.close();
    }

    // ─── Fixed Window Rate Limiter ──────────────────────────────────────────

    @Test
    void shouldAllowWithinLimit_FixedWindow() {
        String key = "rate:user-1:fixed";
        int limit = 5;
        int windowSeconds = 60;

        for (int i = 0; i < 5; i++) {
            assertThat(fixedWindowAllow(key, limit, windowSeconds)).isTrue();
        }
        assertThat(fixedWindowAllow(key, limit, windowSeconds)).isFalse();
    }

    @Test
    void shouldResetAfterWindow_FixedWindow() throws InterruptedException {
        String key = "rate:user-2:fixed";
        int limit = 2;
        int windowSeconds = 1;

        assertThat(fixedWindowAllow(key, limit, windowSeconds)).isTrue();
        assertThat(fixedWindowAllow(key, limit, windowSeconds)).isTrue();
        assertThat(fixedWindowAllow(key, limit, windowSeconds)).isFalse();

        Thread.sleep(1100);
        assertThat(fixedWindowAllow(key, limit, windowSeconds)).isTrue();
    }

    // ─── Sliding Window Log Rate Limiter ────────────────────────────────────

    @Test
    void shouldAllowWithinLimit_SlidingWindow() {
        String key = "rate:user-3:sliding";
        int limit = 3;
        long windowMs = 5000;

        assertThat(slidingWindowAllow(key, limit, windowMs)).isTrue();
        assertThat(slidingWindowAllow(key, limit, windowMs)).isTrue();
        assertThat(slidingWindowAllow(key, limit, windowMs)).isTrue();
        assertThat(slidingWindowAllow(key, limit, windowMs)).isFalse();
    }

    @Test
    void shouldExpireOldEntries_SlidingWindow() throws InterruptedException {
        String key = "rate:user-4:sliding";
        int limit = 2;
        long windowMs = 500;

        assertThat(slidingWindowAllow(key, limit, windowMs)).isTrue();
        assertThat(slidingWindowAllow(key, limit, windowMs)).isTrue();
        assertThat(slidingWindowAllow(key, limit, windowMs)).isFalse();

        Thread.sleep(600);
        assertThat(slidingWindowAllow(key, limit, windowMs)).isTrue();
    }

    // ─── Token Bucket Rate Limiter ──────────────────────────────────────────

    @Test
    void shouldConsumeTokens() {
        String key = "rate:user-5:bucket";
        int capacity = 5;
        int refillRate = 1; // tokens per second

        // Initialize bucket
        initTokenBucket(key, capacity);

        // Consume all tokens
        for (int i = 0; i < 5; i++) {
            assertThat(tokenBucketAllow(key, capacity, refillRate)).isTrue();
        }
        assertThat(tokenBucketAllow(key, capacity, refillRate)).isFalse();
    }

    @Test
    void shouldRefillTokensOverTime() throws InterruptedException {
        String key = "rate:user-6:bucket";
        int capacity = 3;
        int refillRate = 2; // 2 tokens per second

        initTokenBucket(key, capacity);

        // Drain all
        for (int i = 0; i < 3; i++) tokenBucketAllow(key, capacity, refillRate);
        assertThat(tokenBucketAllow(key, capacity, refillRate)).isFalse();

        // Wait for refill (2 tokens/sec → 1 second should give ~2 tokens)
        Thread.sleep(1100);
        assertThat(tokenBucketAllow(key, capacity, refillRate)).isTrue();
    }

    // ─── Implementations ────────────────────────────────────────────────────

    private boolean fixedWindowAllow(String key, int limit, int windowSeconds) {
        long count = jedis.incr(key);
        if (count == 1) {
            jedis.expire(key, windowSeconds);
        }
        return count <= limit;
    }

    private boolean slidingWindowAllow(String key, int limit, long windowMs) {
        long now = System.currentTimeMillis();
        long windowStart = now - windowMs;

        // Lua for atomicity
        String script =
                """
                local key = KEYS[1]
                local now = tonumber(ARGV[1])
                local window_start = tonumber(ARGV[2])
                local limit = tonumber(ARGV[3])

                redis.call('ZREMRANGEBYSCORE', key, '-inf', window_start)
                local count = redis.call('ZCARD', key)

                if count < limit then
                    redis.call('ZADD', key, now, now .. '-' .. math.random(1000000))
                    redis.call('PEXPIRE', key, ARGV[4])
                    return 1
                else
                    return 0
                end
                """;
        Object result = jedis.eval(
                script,
                List.of(key),
                List.of(
                        String.valueOf(now), String.valueOf(windowStart),
                        String.valueOf(limit), String.valueOf(windowMs)));
        return Long.valueOf(1).equals(result);
    }

    private void initTokenBucket(String key, int capacity) {
        jedis.hset(key, "tokens", String.valueOf(capacity));
        jedis.hset(key, "last_refill", String.valueOf(System.currentTimeMillis()));
    }

    private boolean tokenBucketAllow(String key, int capacity, int refillRate) {
        String script =
                """
                local key = KEYS[1]
                local capacity = tonumber(ARGV[1])
                local refill_rate = tonumber(ARGV[2])
                local now = tonumber(ARGV[3])

                local tokens = tonumber(redis.call('HGET', key, 'tokens') or capacity)
                local last_refill = tonumber(redis.call('HGET', key, 'last_refill') or now)

                local elapsed = (now - last_refill) / 1000.0
                local new_tokens = math.min(capacity, tokens + elapsed * refill_rate)

                if new_tokens >= 1 then
                    redis.call('HSET', key, 'tokens', tostring(new_tokens - 1))
                    redis.call('HSET', key, 'last_refill', tostring(now))
                    return 1
                else
                    redis.call('HSET', key, 'tokens', tostring(new_tokens))
                    redis.call('HSET', key, 'last_refill', tostring(now))
                    return 0
                end
                """;
        Object result = jedis.eval(
                script,
                List.of(key),
                List.of(
                        String.valueOf(capacity),
                        String.valueOf(refillRate),
                        String.valueOf(System.currentTimeMillis())));
        return Long.valueOf(1).equals(result);
    }
}
