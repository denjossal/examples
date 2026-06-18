package com.denjossal.study.modern.concurrency;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Demonstrates CompletableFuture patterns for concurrent I/O-bound workloads.
 *
 * When JDK 21 is available, replace the fixed thread pool with:
 *   Executors.newVirtualThreadPerTaskExecutor()
 * Virtual threads are ~1KB (vs ~1MB platform threads), making thread-per-request viable.
 * ExecutorService becomes AutoCloseable in Java 19+, enabling try-with-resources.
 */
public class VirtualThreadDemo {

    private static final Logger logger = Logger.getLogger(VirtualThreadDemo.class.getName());

    public static void main(String[] args) throws Exception {
        compareConcurrencyApproaches(200);
    }

    /**
     * Compares sequential vs concurrent execution for I/O-bound tasks.
     */
    public static long[] compareConcurrencyApproaches(int taskCount) throws Exception {
        long sequentialTime = runSequentially(taskCount);
        long concurrentTime = runWithThreadPool(taskCount);
        return new long[]{sequentialTime, concurrentTime};
    }

    static long runSequentially(int taskCount) {
        Instant start = Instant.now();
        for (int i = 0; i < taskCount; i++) {
            simulateIOTask(i);
        }
        long elapsed = Duration.between(start, Instant.now()).toMillis();
        logger.info("Sequential (%d tasks): %d ms".formatted(taskCount, elapsed));
        return elapsed;
    }

    static long runWithThreadPool(int taskCount) throws Exception {
        Instant start = Instant.now();
        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() * 2
        );

        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            final int id = i;
            futures.add(executor.submit(() -> simulateIOTask(id)));
        }

        for (var future : futures) {
            future.get();
        }
        executor.shutdown();

        long elapsed = Duration.between(start, Instant.now()).toMillis();
        logger.info("Thread pool (%d tasks): %d ms".formatted(taskCount, elapsed));
        return elapsed;
    }

    /**
     * Demonstrates CompletableFuture composition (thenCombine, thenCompose).
     */
    public static CompletableFuture<String> fetchUserProfile(int userId) {
        CompletableFuture<String> nameFuture = CompletableFuture.supplyAsync(
                () -> simulateIOTask(userId)
        );
        CompletableFuture<Integer> ageFuture = CompletableFuture.supplyAsync(
                () -> 25 + (userId % 40)
        );

        return nameFuture.thenCombine(ageFuture,
                (name, age) -> "User %s, age %d".formatted(name, age)
        );
    }

    private static String simulateIOTask(int id) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Result-" + id;
    }
}
