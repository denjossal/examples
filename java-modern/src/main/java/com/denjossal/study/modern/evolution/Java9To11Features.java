package com.denjossal.study.modern.evolution;

import java.util.*;
import java.util.stream.*;

/**
 * Java 9-11 (2017-2018) — Modules, convenience APIs, local var inference.
 *
 * Java 9:  Modules (JPMS), List/Set/Map.of(), Stream improvements, Optional.ifPresentOrElse
 * Java 10: Local variable type inference (var), unmodifiable collectors
 * Java 11: String methods, Files.readString, HttpClient, var in lambdas
 */
public class Java9To11Features {

    // ─── Java 9: Immutable Collection Factories ─────────────────────────────

    public static List<String> immutableList() {
        return List.of("one", "two", "three");
    }

    public static Map<String, Integer> immutableMap() {
        return Map.of("a", 1, "b", 2, "c", 3);
    }

    // ─── Java 9: Stream enhancements ────────────────────────────────────────

    public static List<Integer> takeWhileLessThan(List<Integer> numbers, int limit) {
        return numbers.stream().takeWhile(n -> n < limit).toList();
    }

    public static List<Integer> dropWhileLessThan(List<Integer> numbers, int limit) {
        return numbers.stream().dropWhile(n -> n < limit).toList();
    }

    public static Stream<Integer> iterateWithPredicate(int seed, int max) {
        // Java 9 overload: iterate with hasNext predicate (like a for-loop)
        return Stream.iterate(seed, n -> n < max, n -> n + 2);
    }

    // ─── Java 9: Optional enhancements ──────────────────────────────────────

    public static String optionalOrAction(Optional<String> opt) {
        // ifPresentOrElse: handle both cases
        var result = new StringBuilder();
        opt.ifPresentOrElse(val -> result.append("Found: ").append(val), () -> result.append("Not found"));
        return result.toString();
    }

    public static Optional<String> optionalOr(Optional<String> primary, String fallback) {
        // or(): lazy alternative Optional (not just a value)
        return primary.or(() -> Optional.of(fallback));
    }

    // ─── Java 10: var (local variable type inference) ───────────────────────

    public static List<String> processWithVar(List<String> items) {
        // var infers the type — useful for long generic types
        var result = new ArrayList<String>();
        var filtered =
                items.stream().filter(s -> !s.isBlank()).map(String::trim).toList();
        result.addAll(filtered);
        return result;
    }

    // ─── Java 10: Unmodifiable collectors ───────────────────────────────────

    public static List<Integer> toUnmodifiableList(List<Integer> numbers) {
        return numbers.stream().filter(n -> n > 0).collect(Collectors.toUnmodifiableList());
    }

    // ─── Java 11: String methods ────────────────────────────────────────────

    public static boolean isBlankOrEmpty(String s) {
        return s.isBlank(); // vs isEmpty(): isBlank handles whitespace-only
    }

    public static List<String> splitLines(String text) {
        return text.lines().toList();
    }

    public static String repeatAndStrip(String s, int count) {
        return s.repeat(count).strip(); // strip > trim (Unicode-aware)
    }

    // ─── Java 11: var in lambda parameters ──────────────────────────────────

    public static List<String> annotatedLambda(List<String> items) {
        // var in lambda allows annotations: (@NonNull var x) -> ...
        return items.stream().map((var s) -> s.toLowerCase()).toList();
    }
}
