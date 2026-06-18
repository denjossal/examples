package com.denjossal.study.modern.evolution;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

class Java8FeaturesTest {

    @Test
    void shouldMapWithLambda() {
        var result = Java8Features.map(List.of("a", "bb", "ccc"), String::length);
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    void shouldGroupByLength() {
        var result = Java8Features.groupByLength(List.of("hi", "hey", "hello", "yo"));
        assertThat(result.get(2)).containsExactlyInAnyOrder("hi", "yo");
        assertThat(result.get(3)).containsExactly("hey");
        assertThat(result.get(5)).containsExactly("hello");
    }

    @Test
    void shouldFindFirstEvenSquareOver50() {
        var result = Java8Features.firstEvenSquareOver50(List.of(1, 3, 6, 8, 10));
        assertThat(result).isPresent();
        assertThat(result.getAsInt()).isEqualTo(64); // 8^2
    }

    @Test
    void shouldHandleOptional() {
        var users = Map.of("1", "Alice", "2", "Bob");
        assertThat(Java8Features.getUserDisplayName(users, "1")).isEqualTo("User: Alice");
        assertThat(Java8Features.getUserDisplayName(users, "99")).isEqualTo("Unknown");
    }

    @Test
    void shouldSortUpperCase() {
        assertThat(Java8Features.sortedUpperCase(List.of("banana", "apple", "cherry")))
                .containsExactly("APPLE", "BANANA", "CHERRY");
    }
}
