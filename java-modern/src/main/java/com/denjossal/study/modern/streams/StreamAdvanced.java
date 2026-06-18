package com.denjossal.study.modern.streams;

import java.util.*;
import java.util.stream.*;

/**
 * Advanced Stream API patterns beyond basics — covers:
 * - Collectors (groupingBy, partitioningBy, toUnmodifiableMap)
 * - flatMap for nested structures
 * - reduce with combiner (parallelizable)
 * - teeing collector (Java 12+)
 * - mapMulti (Java 16+)
 */
public class StreamAdvanced {

    public record Order(String customer, List<Item> items) {}
    public record Item(String name, double price, int quantity) {}

    /**
     * groupingBy + downstream collector: revenue per customer.
     */
    public static Map<String, Double> revenueByCustomer(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::customer,
                        Collectors.flatMapping(
                                order -> order.items().stream(),
                                Collectors.summingDouble(item -> item.price() * item.quantity())
                        )
                ));
    }

    /**
     * flatMap: flatten nested items across all orders.
     */
    public static List<String> allItemNames(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.items().stream())
                .map(Item::name)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * partitioningBy: split items into expensive (>100) and cheap.
     */
    public static Map<Boolean, List<Item>> partitionByPrice(List<Item> items, double threshold) {
        return items.stream()
                .collect(Collectors.partitioningBy(item -> item.price() > threshold));
    }

    /**
     * Teeing collector: compute min and max in a single pass.
     */
    public static double[] minMaxPrice(List<Item> items) {
        return items.stream()
                .map(Item::price)
                .collect(Collectors.teeing(
                        Collectors.minBy(Double::compareTo),
                        Collectors.maxBy(Double::compareTo),
                        (min, max) -> new double[]{
                                min.orElse(0.0),
                                max.orElse(0.0)
                        }
                ));
    }

    /**
     * mapMulti (Java 16+): expand each item into multiple elements conditionally.
     * More efficient than flatMap when the mapping is conditional/simple.
     */
    public static List<String> expandHighQuantityItems(List<Item> items) {
        return items.stream()
                .<String>mapMulti((item, consumer) -> {
                    if (item.quantity() > 1) {
                        for (int i = 0; i < item.quantity(); i++) {
                            consumer.accept(item.name() + " #" + (i + 1));
                        }
                    }
                })
                .toList();
    }

    /**
     * reduce with identity, accumulator, and combiner (parallel-safe).
     */
    public static double totalRevenue(List<Order> orders) {
        return orders.parallelStream()
                .flatMap(order -> order.items().stream())
                .reduce(0.0,
                        (sum, item) -> sum + item.price() * item.quantity(),
                        Double::sum
                );
    }
}
