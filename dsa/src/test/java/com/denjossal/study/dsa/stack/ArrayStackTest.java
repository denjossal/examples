package com.denjossal.study.dsa.stack;

import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.assertj.core.api.Assertions.*;

class ArrayStackTest {

    @Test
    void shouldStartEmpty() {
        var stack = new ArrayStack<Integer>();
        assertThat(stack.isEmpty()).isTrue();
        assertThat(stack.size()).isZero();
    }

    @Test
    void shouldPushAndPop() {
        var stack = new ArrayStack<Integer>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertThat(stack.pop()).isEqualTo(3);
        assertThat(stack.pop()).isEqualTo(2);
        assertThat(stack.pop()).isEqualTo(1);
        assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    void shouldPeekWithoutRemoving() {
        var stack = new ArrayStack<String>();
        stack.push("a");
        stack.push("b");

        assertThat(stack.peek()).isEqualTo("b");
        assertThat(stack.size()).isEqualTo(2);
    }

    @Test
    void shouldGrowBeyondInitialCapacity() {
        var stack = new ArrayStack<Integer>();
        for (int i = 0; i < 100; i++) {
            stack.push(i);
        }
        assertThat(stack.size()).isEqualTo(100);
        assertThat(stack.pop()).isEqualTo(99);
    }

    @Test
    void shouldThrowOnPopEmpty() {
        var stack = new ArrayStack<Integer>();
        assertThatThrownBy(stack::pop)
                .isInstanceOf(EmptyStackException.class);
    }

    @Test
    void shouldThrowOnPeekEmpty() {
        var stack = new ArrayStack<Integer>();
        assertThatThrownBy(stack::peek)
                .isInstanceOf(EmptyStackException.class);
    }

    @Test
    void shouldHandlePushPopPushSequence() {
        var stack = new ArrayStack<Integer>();
        stack.push(1);
        stack.push(2);
        stack.pop();
        stack.push(3);

        assertThat(stack.peek()).isEqualTo(3);
        assertThat(stack.size()).isEqualTo(2);
    }
}
