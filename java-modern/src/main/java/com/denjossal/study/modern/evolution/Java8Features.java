package com.denjossal.study.modern.evolution;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Java 8 (2014) — The functional revolution.
 * Key additions: lambdas, Stream API, Optional, method references,
 * default methods, CompletableFuture, new Date/Time API.
 */
public class Java8Features {

    // ─── Lambdas & Functional Interfaces ────────────────────────────────────

    @FunctionalInterface
    public interface Transformer<T, R> {
        R transform(T input);
    }

    public static <T, R> List<R> map(List<T> list, Transformer<T, R> fn) {
        return list.stream().map(fn::transform).toList();
    }

    // ─── Stream API ─────────────────────────────────────────────────────────

    public static Map<Integer, List<String>> groupByLength(List<String> words) {
        return words.stream()
                .collect(Collectors.groupingBy(String::length));
    }

    public static OptionalInt firstEvenSquareOver50(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n * n)
                .filter(sq -> sq > 50)
                .findFirst();
    }

    // ─── Optional ───────────────────────────────────────────────────────────

    public static String getUserDisplayName(Map<String, String> users, String id) {
        return Optional.ofNullable(users.get(id))
                .map(name -> "User: " + name)
                .orElse("Unknown");
    }

    // ─── Method References ──────────────────────────────────────────────────

    public static List<String> sortedUpperCase(List<String> words) {
        return words.stream()
                .map(String::toUpperCase)
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    // ─── Default Methods (interface evolution without breaking impls) ────────

    public interface Greetable {
        String name();

        default String greet() {
            return "Hello, " + name() + "!";
        }
    }
}
