package com.denjossal.study.modern.evolution;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Java 22-25 (2024-2025) — Unnamed variables, statements before super,
 * Gatherers (stream intermediary), structured concurrency, scoped values.
 *
 * Java 22: Unnamed variables (_), statements before super() (preview)
 * Java 23: Primitive types in patterns (preview), markdown doc-comments
 * Java 24: Stream Gatherers (final), flexible constructor bodies
 * Java 25: Stable value types, compact source files (preview)
 */
public class Java22To25Features {

    // ─── Java 22: Unnamed Variables (_) ─────────────────────────────────────

    public static int countElements(List<?> list) {
        int count = 0;
        for (var _ : list) {  // we don't use the element, so _ signals intent
            count++;
        }
        return count;
    }

    public static List<Integer> extractIndices(Map<Integer, String> map) {
        // _ in structured bindings: we only care about keys
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().startsWith("A"))
                .map(entry -> entry.getKey())
                .toList();
    }

    // Unnamed in catch — we don't use the exception
    public static Optional<Integer> safeParse(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException _) {
            return Optional.empty();
        }
    }

    // ─── Java 22/24: Statements Before super() ─────────────────────────────

    // Before Java 22: could NOT have statements before super()
    // Now you can validate/transform args before calling super
    public static class ValidatedList<E> extends ArrayList<E> {
        public ValidatedList(Collection<E> items) {
            // Java 24: can validate before calling super
            Objects.requireNonNull(items, "items must not be null");
            super(items);
        }
    }

    // ─── Java 24: Stream Gatherers (JEP 485, final) ─────────────────────────

    /**
     * Gatherers are custom intermediate operations for streams.
     * They fill the gap between map/filter and full collectors.
     * Built-in gatherers: windowFixed, windowSliding, fold, scan, mapConcurrent
     */
    public static List<List<Integer>> windowedChunks(List<Integer> numbers, int windowSize) {
        return numbers.stream()
                .gather(Gatherers.windowFixed(windowSize))
                .toList();
    }

    public static List<List<Integer>> slidingWindows(List<Integer> numbers, int windowSize) {
        return numbers.stream()
                .gather(Gatherers.windowSliding(windowSize))
                .toList();
    }

    /**
     * fold: like reduce but produces intermediate results at each step.
     */
    public static List<Integer> runningSum(List<Integer> numbers) {
        return numbers.stream()
                .gather(Gatherers.fold(() -> 0, Integer::sum))
                .toList();
    }

    /**
     * scan: like fold but emits each intermediate value.
     */
    public static List<Integer> prefixSums(List<Integer> numbers) {
        return numbers.stream()
                .gather(Gatherers.scan(() -> 0, Integer::sum))
                .toList();
    }

    /**
     * mapConcurrent: parallel map with bounded concurrency — great for I/O.
     * This is like virtual threads + a concurrency limit built into streams.
     */
    public static List<String> fetchConcurrently(List<String> urls, int maxConcurrency) {
        return urls.stream()
                .gather(Gatherers.mapConcurrent(maxConcurrency, url -> {
                    try { Thread.sleep(10); } catch (InterruptedException _) {}
                    return "Response: " + url;
                }))
                .toList();
    }

    // ─── Java 21+: Structured Concurrency (final in 25) ────────────────────

    public record UserProfile(String name, int age, String email) {}

    /**
     * Structured concurrency (Java 25 final API):
     * StructuredTaskScope.open() with a Joiner that collects all results.
     * If one fails, siblings are cancelled automatically.
     */
    public static UserProfile fetchProfile(int userId) throws Exception {
        try (var scope = StructuredTaskScope.open()) {
            var nameFuture = scope.fork(() -> fetchName(userId));
            var ageFuture = scope.fork(() -> fetchAge(userId));
            var emailFuture = scope.fork(() -> fetchEmail(userId));

            scope.join();

            return new UserProfile(
                    nameFuture.get(),
                    ageFuture.get(),
                    emailFuture.get()
            );
        }
    }

    private static String fetchName(int id) throws InterruptedException {
        Thread.sleep(20);
        return "User-" + id;
    }

    private static int fetchAge(int id) throws InterruptedException {
        Thread.sleep(15);
        return 25 + (id % 40);
    }

    private static String fetchEmail(int id) throws InterruptedException {
        Thread.sleep(10);
        return "user" + id + "@example.com";
    }
}
