package com.denjossal.study.dsa.list;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class DoublyLinkedListTest {

    @Test
    void shouldStartEmpty() {
        var list = new DoublyLinkedList<Integer>();
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.size()).isZero();
    }

    @Test
    void shouldAddFirst() {
        var list = new DoublyLinkedList<Integer>();
        list.addFirst(3);
        list.addFirst(2);
        list.addFirst(1);

        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(1)).isEqualTo(2);
        assertThat(list.get(2)).isEqualTo(3);
    }

    @Test
    void shouldAddLast() {
        var list = new DoublyLinkedList<String>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");

        assertThat(list.get(0)).isEqualTo("a");
        assertThat(list.get(2)).isEqualTo("c");
    }

    @Test
    void shouldRemoveFirst() {
        var list = new DoublyLinkedList<Integer>();
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);

        assertThat(list.removeFirst()).isEqualTo(10);
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(20);
    }

    @Test
    void shouldRemoveLast() {
        var list = new DoublyLinkedList<Integer>();
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);

        assertThat(list.removeLast()).isEqualTo(30);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void shouldRemoveByIndex() {
        var list = new DoublyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addLast(4);

        assertThat(list.remove(1)).isEqualTo(2);
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(1)).isEqualTo(3);
    }

    @Test
    void shouldSetElement() {
        var list = new DoublyLinkedList<String>();
        list.addLast("x");
        list.addLast("y");

        list.set(1, "z");

        assertThat(list.get(1)).isEqualTo("z");
    }

    @Test
    void shouldContainElement() {
        var list = new DoublyLinkedList<String>();
        list.addLast("hello");
        list.addLast("world");

        assertThat(list.contains("hello")).isTrue();
        assertThat(list.contains("missing")).isFalse();
    }

    @Test
    void shouldThrowOnEmptyRemove() {
        var list = new DoublyLinkedList<Integer>();

        assertThatThrownBy(list::removeFirst)
                .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(list::removeLast)
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldThrowOnInvalidIndex() {
        var list = new DoublyLinkedList<Integer>();
        list.addFirst(1);

        assertThatThrownBy(() -> list.get(5))
                .isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.get(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void shouldAccessFromTailForHighIndices() {
        var list = new DoublyLinkedList<Integer>();
        for (int i = 0; i < 10; i++) list.addLast(i);

        assertThat(list.get(8)).isEqualTo(8);
        assertThat(list.get(9)).isEqualTo(9);
    }
}
