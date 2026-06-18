package com.denjossal.study.dsa.heap;

import static org.assertj.core.api.Assertions.*;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class MinHeapTest {

    @Test
    void shouldStartEmpty() {
        var heap = new MinHeap<Integer>();
        assertThat(heap.isEmpty()).isTrue();
        assertThat(heap.size()).isZero();
    }

    @Test
    void shouldOfferAndPeek() {
        var heap = new MinHeap<Integer>();
        heap.offer(5);
        heap.offer(3);
        heap.offer(7);

        assertThat(heap.peek()).isEqualTo(3);
        assertThat(heap.size()).isEqualTo(3);
    }

    @Test
    void shouldPollInOrder() {
        var heap = new MinHeap<Integer>();
        heap.offer(5);
        heap.offer(1);
        heap.offer(3);
        heap.offer(2);
        heap.offer(4);

        assertThat(heap.poll()).isEqualTo(1);
        assertThat(heap.poll()).isEqualTo(2);
        assertThat(heap.poll()).isEqualTo(3);
        assertThat(heap.poll()).isEqualTo(4);
        assertThat(heap.poll()).isEqualTo(5);
        assertThat(heap.isEmpty()).isTrue();
    }

    @Test
    void shouldBuildFromArray() {
        Integer[] elements = {9, 4, 7, 1, 3, 8, 2};
        var heap = new MinHeap<>(elements);

        assertThat(heap.size()).isEqualTo(7);
        assertThat(heap.poll()).isEqualTo(1);
        assertThat(heap.poll()).isEqualTo(2);
        assertThat(heap.poll()).isEqualTo(3);
    }

    @Test
    void shouldWorkAsMaxHeapWithReverseComparator() {
        var maxHeap = new MinHeap<Integer>(Comparator.reverseOrder());
        maxHeap.offer(1);
        maxHeap.offer(5);
        maxHeap.offer(3);

        assertThat(maxHeap.poll()).isEqualTo(5);
        assertThat(maxHeap.poll()).isEqualTo(3);
        assertThat(maxHeap.poll()).isEqualTo(1);
    }

    @Test
    void shouldGrowBeyondCapacity() {
        var heap = new MinHeap<Integer>();
        for (int i = 100; i > 0; i--) {
            heap.offer(i);
        }
        assertThat(heap.size()).isEqualTo(100);
        assertThat(heap.peek()).isEqualTo(1);
    }

    @Test
    void shouldThrowOnPollEmpty() {
        var heap = new MinHeap<Integer>();
        assertThatThrownBy(heap::poll).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldThrowOnPeekEmpty() {
        var heap = new MinHeap<Integer>();
        assertThatThrownBy(heap::peek).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldHandleDuplicates() {
        var heap = new MinHeap<Integer>();
        heap.offer(3);
        heap.offer(3);
        heap.offer(3);

        assertThat(heap.size()).isEqualTo(3);
        assertThat(heap.poll()).isEqualTo(3);
        assertThat(heap.poll()).isEqualTo(3);
        assertThat(heap.poll()).isEqualTo(3);
    }

    @Test
    void shouldWorkWithStrings() {
        var heap = new MinHeap<String>();
        heap.offer("banana");
        heap.offer("apple");
        heap.offer("cherry");

        assertThat(heap.poll()).isEqualTo("apple");
        assertThat(heap.poll()).isEqualTo("banana");
        assertThat(heap.poll()).isEqualTo("cherry");
    }
}
