package com.denjossal.study.aws.dynamodb;

import java.util.*;
import java.util.stream.*;

/**
 * DynamoDB Single-Table Design patterns.
 *
 * Key concepts:
 * - Partition key (PK) + Sort key (SK) as composite primary key
 * - Access patterns drive the schema (not entities)
 * - GSI (Global Secondary Index) for alternate access patterns
 * - Overloaded keys: PK="USER#123", SK="PROFILE" or SK="ORDER#456"
 *
 * This simulates DynamoDB behavior in-memory to demonstrate the patterns.
 */
public class SingleTableDesign {

    public record Item(String pk, String sk, Map<String, Object> attributes) {}

    private final List<Item> table = new ArrayList<>();

    public void putItem(String pk, String sk, Map<String, Object> attributes) {
        table.removeIf(item -> item.pk().equals(pk) && item.sk().equals(sk));
        var allAttrs = new HashMap<>(attributes);
        allAttrs.put("PK", pk);
        allAttrs.put("SK", sk);
        table.add(new Item(pk, sk, allAttrs));
    }

    /**
     * Get a single item by PK + SK (O(1) in real DynamoDB).
     */
    public Optional<Item> getItem(String pk, String sk) {
        return table.stream()
                .filter(item -> item.pk().equals(pk) && item.sk().equals(sk))
                .findFirst();
    }

    /**
     * Query: all items with a given PK, optionally filtered by SK prefix.
     * In DynamoDB: efficiently fetches all items in the same partition.
     */
    public List<Item> query(String pk, String skPrefix) {
        return table.stream()
                .filter(item -> item.pk().equals(pk))
                .filter(item -> skPrefix == null || item.sk().startsWith(skPrefix))
                .sorted(Comparator.comparing(Item::sk))
                .toList();
    }

    /**
     * Query all items with a given PK.
     */
    public List<Item> query(String pk) {
        return query(pk, null);
    }

    // ─── E-Commerce Domain Helpers ──────────────────────────────────────────

    /**
     * Access pattern: Create a user profile.
     * PK: USER#userId, SK: PROFILE
     */
    public void createUser(String userId, String name, String email) {
        putItem("USER#" + userId, "PROFILE",
                Map.of("name", name, "email", email, "type", "User"));
    }

    /**
     * Access pattern: Place an order for a user.
     * PK: USER#userId, SK: ORDER#orderId
     */
    public void createOrder(String userId, String orderId, double total, String status) {
        putItem("USER#" + userId, "ORDER#" + orderId,
                Map.of("total", total, "status", status, "type", "Order"));
    }

    /**
     * Access pattern: Get user profile.
     */
    public Optional<Item> getUserProfile(String userId) {
        return getItem("USER#" + userId, "PROFILE");
    }

    /**
     * Access pattern: Get all orders for a user (sorted by orderId).
     */
    public List<Item> getUserOrders(String userId) {
        return query("USER#" + userId, "ORDER#");
    }

    /**
     * Access pattern: Get a specific order.
     */
    public Optional<Item> getOrder(String userId, String orderId) {
        return getItem("USER#" + userId, "ORDER#" + orderId);
    }

    public int itemCount() {
        return table.size();
    }
}
