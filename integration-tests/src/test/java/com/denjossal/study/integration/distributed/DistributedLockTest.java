package com.denjossal.study.integration.distributed;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Distributed Lock with Redis — ensures mutual exclusion across multiple service instances.
 *
 * Pattern: SET key value NX EX ttl (atomic acquire)
 * - NX: only set if not exists (atomic check-and-set)
 * - EX: auto-expire to prevent deadlocks if holder crashes
 * - Value: unique token so only the holder can release
 *
 * Use cases: leader election, resource reservation, preventing duplicate processing.
 * In production: use Redlock (multi-node) or Redisson for robustness.
 */
@Testcontainers
class DistributedLockTest {

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    private String redisHost;
    private int redisPort;

    @BeforeEach
    void setUp() {
        redisHost = redis.getHost();
        redisPort = redis.getMappedPort(6379);
    }

    @Test
    void shouldAcquireAndReleaseLock() {
        try (var jedis = new Jedis(redisHost, redisPort)) {
            String lockKey = "lock:resource-1";
            String token = UUID.randomUUID().toString();

            boolean acquired = tryAcquire(jedis, lockKey, token, 10);
            assertThat(acquired).isTrue();

            boolean released = release(jedis, lockKey, token);
            assertThat(released).isTrue();
        }
    }

    @Test
    void shouldPreventDoubleAcquire() {
        try (var jedis = new Jedis(redisHost, redisPort)) {
            String lockKey = "lock:resource-2";
            String token1 = "holder-1";
            String token2 = "holder-2";

            boolean first = tryAcquire(jedis, lockKey, token1, 10);
            boolean second = tryAcquire(jedis, lockKey, token2, 10);

            assertThat(first).isTrue();
            assertThat(second).isFalse();

            release(jedis, lockKey, token1);
        }
    }

    @Test
    void shouldOnlyAllowHolderToRelease() {
        try (var jedis = new Jedis(redisHost, redisPort)) {
            String lockKey = "lock:resource-3";
            String holder = "real-holder";
            String impostor = "fake-holder";

            tryAcquire(jedis, lockKey, holder, 10);

            boolean impostorRelease = release(jedis, lockKey, impostor);
            assertThat(impostorRelease).isFalse();

            // Lock still held
            assertThat(tryAcquire(jedis, lockKey, "anyone", 10)).isFalse();

            // Real holder can release
            assertThat(release(jedis, lockKey, holder)).isTrue();
        }
    }

    @Test
    void shouldAutoExpireLock() throws InterruptedException {
        try (var jedis = new Jedis(redisHost, redisPort)) {
            String lockKey = "lock:resource-4";
            tryAcquire(jedis, lockKey, "holder", 1); // 1 second TTL

            Thread.sleep(1500);

            // Lock expired, new holder can acquire
            boolean acquired = tryAcquire(jedis, lockKey, "new-holder", 10);
            assertThat(acquired).isTrue();
        }
    }

    @Test
    void shouldEnsureMutualExclusionUnderConcurrency() throws Exception {
        String lockKey = "lock:counter";
        var counter = new AtomicInteger(0);
        int threads = 10;
        int incrementsPerThread = 50;

        var executor = Executors.newFixedThreadPool(threads);
        var latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                try (var jedis = new Jedis(redisHost, redisPort)) {
                    for (int i = 0; i < incrementsPerThread; i++) {
                        String token = UUID.randomUUID().toString();
                        while (!tryAcquire(jedis, lockKey, token, 5)) {
                            Thread.sleep(5);
                        }
                        try {
                            counter.incrementAndGet();
                        } finally {
                            release(jedis, lockKey, token);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(counter.get()).isEqualTo(threads * incrementsPerThread);
    }

    // ─── Lock implementation ────────────────────────────────────────────────

    private boolean tryAcquire(Jedis jedis, String key, String token, long ttlSeconds) {
        String result = jedis.set(key, token, SetParams.setParams().nx().ex(ttlSeconds));
        return "OK".equals(result);
    }

    private boolean release(Jedis jedis, String key, String expectedToken) {
        // Lua script ensures atomic check-and-delete (no race between GET and DEL)
        String script = """
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                else
                    return 0
                end
                """;
        Object result = jedis.eval(script, java.util.List.of(key), java.util.List.of(expectedToken));
        return Long.valueOf(1).equals(result);
    }
}
