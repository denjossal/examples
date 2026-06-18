package com.denjossal.study.dsa.queue;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class CircularArrayQueueTest {

    @Test
    void shouldStartEmpty() {
        var queue = new CircularArrayQueue<Integer>();
        assertThat(queue.isEmpty()).isTrue();
        assertThat(queue.size()).isZero();
    }

    @Test
    void shouldEnqueueAndDequeue() {
        var queue = new CircularArrayQueue<Integer>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        assertThat(queue.dequeue()).isEqualTo(1);
        assertThat(queue.dequeue()).isEqualTo(2);
        assertThat(queue.dequeue()).isEqualTo(3);
        assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    void shouldPeekWithoutRemoving() {
        var queue = new CircularArrayQueue<String>();
        queue.enqueue("first");
        queue.enqueue("second");

        assertThat(queue.peek()).isEqualTo("first");
        assertThat(queue.size()).isEqualTo(2);
    }

    @Test
    void shouldWrapAroundCircularly() {
        var queue = new CircularArrayQueue<Integer>();
        // Fill and partially drain to advance head pointer
        for (int i = 0; i < 8; i++) queue.enqueue(i);
        for (int i = 0; i < 5; i++) queue.dequeue();

        // Now head is at index 5, enqueue more to wrap around
        for (int i = 100; i < 108; i++) queue.enqueue(i);

        // Should dequeue in FIFO order
        assertThat(queue.dequeue()).isEqualTo(5);
        assertThat(queue.dequeue()).isEqualTo(6);
        assertThat(queue.dequeue()).isEqualTo(7);
        assertThat(queue.dequeue()).isEqualTo(100);
    }

    @Test
    void shouldGrowWhenFull() {
        var queue = new CircularArrayQueue<Integer>();
        for (int i = 0; i < 20; i++) {
            queue.enqueue(i);
        }
        assertThat(queue.size()).isEqualTo(20);
        assertThat(queue.dequeue()).isEqualTo(0);
        assertThat(queue.dequeue()).isEqualTo(1);
    }

    @Test
    void shouldThrowOnDequeueEmpty() {
        var queue = new CircularArrayQueue<Integer>();
        assertThatThrownBy(queue::dequeue).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldThrowOnPeekEmpty() {
        var queue = new CircularArrayQueue<Integer>();
        assertThatThrownBy(queue::peek).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldHandleEnqueueDequeueInterleaved() {
        var queue = new CircularArrayQueue<Integer>();
        queue.enqueue(1);
        queue.enqueue(2);
        assertThat(queue.dequeue()).isEqualTo(1);
        queue.enqueue(3);
        assertThat(queue.dequeue()).isEqualTo(2);
        assertThat(queue.dequeue()).isEqualTo(3);
        assertThat(queue.isEmpty()).isTrue();
    }
}
