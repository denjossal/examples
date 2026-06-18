package com.denjossal.study.modern.streams;

import static org.assertj.core.api.Assertions.*;

import com.denjossal.study.modern.streams.StreamAdvanced.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class StreamAdvancedTest {

    private final List<Order> sampleOrders = List.of(
            new Order("Alice", List.of(new Item("Widget", 10.0, 3), new Item("Gadget", 25.0, 1))),
            new Order("Bob", List.of(new Item("Widget", 10.0, 2), new Item("Gizmo", 50.0, 1))),
            new Order("Alice", List.of(new Item("Gizmo", 50.0, 2))));

    @Test
    void shouldComputeRevenueByCustomer() {
        var revenue = StreamAdvanced.revenueByCustomer(sampleOrders);

        assertThat(revenue.get("Alice")).isEqualTo(155.0); // 30 + 25 + 100
        assertThat(revenue.get("Bob")).isEqualTo(70.0); // 20 + 50
    }

    @Test
    void shouldFlattenAllItemNames() {
        var names = StreamAdvanced.allItemNames(sampleOrders);

        assertThat(names).containsExactly("Gadget", "Gizmo", "Widget");
    }

    @Test
    void shouldPartitionByPrice() {
        var items = List.of(new Item("Cheap", 5.0, 1), new Item("Mid", 50.0, 1), new Item("Expensive", 200.0, 1));

        var result = StreamAdvanced.partitionByPrice(items, 100.0);

        assertThat(result.get(true)).hasSize(1);
        assertThat(result.get(false)).hasSize(2);
    }

    @Test
    void shouldComputeMinMaxPrice() {
        var items = List.of(new Item("A", 5.0, 1), new Item("B", 100.0, 1), new Item("C", 42.0, 1));

        double[] minMax = StreamAdvanced.minMaxPrice(items);

        assertThat(minMax[0]).isEqualTo(5.0);
        assertThat(minMax[1]).isEqualTo(100.0);
    }

    @Test
    void shouldExpandHighQuantityItems() {
        var items = List.of(new Item("Solo", 10.0, 1), new Item("Multi", 20.0, 3));

        var expanded = StreamAdvanced.expandHighQuantityItems(items);

        assertThat(expanded).containsExactly("Multi #1", "Multi #2", "Multi #3");
    }

    @Test
    void shouldComputeTotalRevenue() {
        double total = StreamAdvanced.totalRevenue(sampleOrders);
        assertThat(total).isEqualTo(225.0); // 30+25+20+50+100
    }
}
