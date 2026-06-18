package com.denjossal.study.aws.dynamodb;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SingleTableDesignTest {

    private SingleTableDesign db;

    @BeforeEach
    void setUp() {
        db = new SingleTableDesign();
        db.createUser("user-1", "Alice", "alice@test.com");
        db.createUser("user-2", "Bob", "bob@test.com");
        db.createOrder("user-1", "order-001", 99.99, "PLACED");
        db.createOrder("user-1", "order-002", 250.00, "SHIPPED");
        db.createOrder("user-2", "order-003", 15.00, "DELIVERED");
    }

    @Test
    void shouldGetUserProfile() {
        var profile = db.getUserProfile("user-1");

        assertThat(profile).isPresent();
        assertThat(profile.get().attributes().get("name")).isEqualTo("Alice");
        assertThat(profile.get().pk()).isEqualTo("USER#user-1");
        assertThat(profile.get().sk()).isEqualTo("PROFILE");
    }

    @Test
    void shouldReturnEmptyForMissingUser() {
        assertThat(db.getUserProfile("nonexistent")).isEmpty();
    }

    @Test
    void shouldGetAllOrdersForUser() {
        var orders = db.getUserOrders("user-1");

        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).sk()).isEqualTo("ORDER#order-001");
        assertThat(orders.get(1).sk()).isEqualTo("ORDER#order-002");
    }

    @Test
    void shouldGetSpecificOrder() {
        var order = db.getOrder("user-1", "order-001");

        assertThat(order).isPresent();
        assertThat(order.get().attributes().get("total")).isEqualTo(99.99);
        assertThat(order.get().attributes().get("status")).isEqualTo("PLACED");
    }

    @Test
    void shouldQueryAllItemsForPartition() {
        var allUser1Items = db.query("USER#user-1");

        // Profile + 2 orders = 3 items
        assertThat(allUser1Items).hasSize(3);
    }

    @Test
    void shouldQueryWithSkPrefix() {
        var onlyOrders = db.query("USER#user-1", "ORDER#");
        assertThat(onlyOrders).hasSize(2);

        var onlyProfile = db.query("USER#user-1", "PROFILE");
        assertThat(onlyProfile).hasSize(1);
    }

    @Test
    void shouldOverwriteOnPut() {
        db.createOrder("user-1", "order-001", 150.00, "CANCELLED");

        var order = db.getOrder("user-1", "order-001");
        assertThat(order.get().attributes().get("total")).isEqualTo(150.00);
        assertThat(order.get().attributes().get("status")).isEqualTo("CANCELLED");

        // Total count shouldn't increase
        assertThat(db.getUserOrders("user-1")).hasSize(2);
    }

    @Test
    void shouldMaintainTotalItemCount() {
        // 2 users + 3 orders = 5
        assertThat(db.itemCount()).isEqualTo(5);
    }
}
