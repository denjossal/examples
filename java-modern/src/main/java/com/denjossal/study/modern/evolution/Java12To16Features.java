package com.denjossal.study.modern.evolution;

import java.util.*;
import java.util.stream.*;

/**
 * Java 12-16 (2019-2021) — Switch expressions, text blocks, records, instanceof patterns.
 *
 * Java 12: Switch expressions (preview)
 * Java 13: Text blocks (preview)
 * Java 14: Switch expressions (final), helpful NPE messages, Records (preview)
 * Java 15: Text blocks (final), sealed classes (preview)
 * Java 16: Records (final), instanceof pattern matching (final), Stream.toList()
 */
public class Java12To16Features {

    // ─── Java 14: Switch Expressions (arrow syntax, yield) ──────────────────

    public static String dayType(String day) {
        return switch (day.toUpperCase()) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
            case "SATURDAY", "SUNDAY" -> "Weekend";
            default -> "Unknown";
        };
    }

    public static int numericDayValue(String day) {
        return switch (day.toUpperCase()) {
            case "MONDAY" -> 1;
            case "TUESDAY" -> 2;
            case "WEDNESDAY" -> 3;
            case "THURSDAY" -> 4;
            case "FRIDAY" -> 5;
            case "SATURDAY" -> 6;
            case "SUNDAY" -> 7;
            default -> {
                // yield for multi-line case blocks
                System.err.println("Unknown day: " + day);
                yield -1;
            }
        };
    }

    // ─── Java 15: Text Blocks ───────────────────────────────────────────────

    public static String jsonTemplate(String name, int age) {
        return """
                {
                    "name": "%s",
                    "age": %d,
                    "active": true
                }
                """.formatted(name, age);
    }

    public static String sqlQuery() {
        return """
                SELECT u.name, u.email, o.total
                FROM users u
                JOIN orders o ON u.id = o.user_id
                WHERE o.total > 100
                ORDER BY o.total DESC
                """;
    }

    // ─── Java 16: Records ───────────────────────────────────────────────────

    // Records = immutable data carriers with auto-generated:
    // constructor, getters, equals, hashCode, toString
    public record Money(double amount, String currency) {
        // Compact constructor for validation
        public Money {
            if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
            currency = currency.toUpperCase();
        }

        public Money add(Money other) {
            if (!this.currency.equals(other.currency)) {
                throw new IllegalArgumentException("Currency mismatch");
            }
            return new Money(this.amount + other.amount, this.currency);
        }
    }

    public record Range(int start, int end) {
        public Range {
            if (start > end) throw new IllegalArgumentException("start must be <= end");
        }

        public boolean contains(int value) {
            return value >= start && value <= end;
        }

        public int length() {
            return end - start;
        }
    }

    // ─── Java 16: instanceof Pattern Matching ───────────────────────────────

    // Before Java 16:
    public static String describeOldWay(Object obj) {
        if (obj instanceof String) {
            String s = (String) obj;  // explicit cast required
            return "String of length " + s.length();
        }
        return "Not a string";
    }

    // Java 16+: pattern variable eliminates the cast
    public static String describeNewWay(Object obj) {
        if (obj instanceof String s) {
            return "String of length " + s.length();
        }
        if (obj instanceof Integer i && i > 0) {
            return "Positive integer: " + i;
        }
        if (obj instanceof List<?> list && !list.isEmpty()) {
            return "Non-empty list of size " + list.size();
        }
        return "Something else: " + obj.getClass().getSimpleName();
    }

    // ─── Java 16: Stream.toList() ───────────────────────────────────────────

    // Before: .collect(Collectors.toList()) — mutable
    // After:  .toList() — unmodifiable, more concise
    public static List<String> filterAndCollect(List<String> items, String prefix) {
        return items.stream()
                .filter(s -> s.startsWith(prefix))
                .toList(); // returns unmodifiable list
    }

    // ─── Java 12: Collectors.teeing ─────────────────────────────────────────

    public record Stats(long count, double average) {}

    public static Stats computeStats(List<Integer> numbers) {
        return numbers.stream().collect(
                Collectors.teeing(
                        Collectors.counting(),
                        Collectors.averagingInt(Integer::intValue),
                        Stats::new
                )
        );
    }
}
