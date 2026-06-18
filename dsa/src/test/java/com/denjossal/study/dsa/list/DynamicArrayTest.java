package com.denjossal.study.dsa.list;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DynamicArrayTest {

    @Test
    void shouldStartEmpty() {
        var arr = new DynamicArray<Integer>();
        assertThat(arr.size()).isZero();
        assertThat(arr.isEmpty()).isTrue();
    }

    @Test
    void shouldAddAndGet() {
        var arr = new DynamicArray<String>();
        arr.add("hello");
        arr.add("world");

        assertThat(arr.size()).isEqualTo(2);
        assertThat(arr.get(0)).isEqualTo("hello");
        assertThat(arr.get(1)).isEqualTo("world");
    }

    @Test
    void shouldGrowBeyondInitialCapacity() {
        var arr = new DynamicArray<Integer>(2);
        arr.add(1);
        arr.add(2);
        arr.add(3);

        assertThat(arr.size()).isEqualTo(3);
        assertThat(arr.capacity()).isEqualTo(4);
        assertThat(arr.get(2)).isEqualTo(3);
    }

    @Test
    void shouldRemoveAndShift() {
        var arr = new DynamicArray<Integer>();
        arr.add(10);
        arr.add(20);
        arr.add(30);

        int removed = arr.remove(1);

        assertThat(removed).isEqualTo(20);
        assertThat(arr.size()).isEqualTo(2);
        assertThat(arr.get(0)).isEqualTo(10);
        assertThat(arr.get(1)).isEqualTo(30);
    }

    @Test
    void shouldSetElement() {
        var arr = new DynamicArray<String>();
        arr.add("a");
        arr.add("b");

        arr.set(1, "c");

        assertThat(arr.get(1)).isEqualTo("c");
    }

    @Test
    void shouldThrowOnInvalidIndex() {
        var arr = new DynamicArray<Integer>();
        arr.add(1);

        assertThatThrownBy(() -> arr.get(5)).isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> arr.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void shouldThrowOnNegativeCapacity() {
        assertThatThrownBy(() -> new DynamicArray<>(-1)).isInstanceOf(IllegalArgumentException.class);
    }
}
