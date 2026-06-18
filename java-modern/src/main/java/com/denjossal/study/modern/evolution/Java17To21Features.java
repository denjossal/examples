package com.denjossal.study.modern.evolution;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Java 17-21 (2021-2023) — Sealed classes, pattern matching for switch, record patterns,
 * virtual threads, sequenced collections, string templates.
 *
 * Java 17: Sealed classes (final), pattern matching for switch (preview)
 * Java 19: Virtual threads (preview), structured concurrency (incubator)
 * Java 21: Pattern matching for switch (final), record patterns (final),
 *           virtual threads (final), sequenced collections, string templates (preview)
 */
public class Java17To21Features {

    // ─── Java 17: Sealed Classes ────────────────────────────────────────────

    public sealed interface Result<T> permits Success, Failure, Pending {}
    public record Success<T>(T value) implements Result<T> {}
    public record Failure<T>(String error, Exception cause) implements Result<T> {}
    public record Pending<T>(String taskId) implements Result<T> {}

    // ─── Java 21: Pattern Matching for Switch (final) ───────────────────────

    public static <T> String describeResult(Result<T> result) {
        return switch (result) {
            case Success<T> s -> "Success: " + s.value();
            case Failure<T> f -> "Failed: " + f.error();
            case Pending<T> p -> "Pending: " + p.taskId();
        };
    }

    // Guarded patterns with 'when' clause
    public static String classifyNumber(Object obj) {
        return switch (obj) {
            case Integer i when i < 0 -> "negative";
            case Integer i when i == 0 -> "zero";
            case Integer i -> "positive: " + i;
            case Double d when d.isNaN() -> "NaN";
            case Double d -> "double: " + d;
            case String s -> "string: " + s;
            case null -> "null";
            default -> "other: " + obj.getClass().getSimpleName();
        };
    }

    // ─── Java 21: Record Patterns (deconstruction in switch) ────────────────

    public sealed interface Shape permits Circle, Rect, Triangle {}
    public record Circle(double radius) implements Shape {}
    public record Rect(double w, double h) implements Shape {}
    public record Triangle(double base, double height) implements Shape {}

    public static double area(Shape shape) {
        return switch (shape) {
            case Circle(var r) -> Math.PI * r * r;
            case Rect(var w, var h) -> w * h;
            case Triangle(var b, var h) -> 0.5 * b * h;
        };
    }

    // Nested record patterns
    public record Point(double x, double y) {}
    public record Line(Point start, Point end) {}

    public static double lineLength(Object obj) {
        return switch (obj) {
            case Line(Point(var x1, var y1), Point(var x2, var y2)) ->
                    Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            default -> 0.0;
        };
    }

    // ─── Java 21: Virtual Threads ───────────────────────────────────────────

    public static List<String> fetchAllWithVirtualThreads(List<String> urls) throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = urls.stream()
                    .map(url -> executor.submit(() -> simulateFetch(url)))
                    .toList();

            var results = new ArrayList<String>();
            for (var future : futures) {
                results.add(future.get());
            }
            return results;
        }
    }

    // ─── Java 21: Sequenced Collections ─────────────────────────────────────

    public static <T> T getFirst(SequencedCollection<T> collection) {
        return collection.getFirst();
    }

    public static <T> T getLast(SequencedCollection<T> collection) {
        return collection.getLast();
    }

    public static <T> SequencedCollection<T> reversed(SequencedCollection<T> collection) {
        return collection.reversed();
    }

    public static void sequencedCollectionsDemo() {
        // All ordered collections now share a common interface
        var list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        var deque = new ArrayDeque<>(List.of("a", "b", "c"));
        var linkedSet = new LinkedHashSet<>(Set.of("x", "y", "z"));

        // Uniform API across all:
        list.getFirst();      // 1
        list.getLast();       // 5
        deque.reversed();     // [c, b, a]
        list.addFirst(0);    // [0, 1, 2, 3, 4, 5]
    }

    private static String simulateFetch(String url) {
        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return "Response from " + url;
    }
}
