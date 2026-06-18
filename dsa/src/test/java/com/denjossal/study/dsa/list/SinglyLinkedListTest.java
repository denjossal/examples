package com.denjossal.study.dsa.list;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class SinglyLinkedListTest {

    @Test
    void shouldStartEmpty() {
        var list = new SinglyLinkedList<Integer>();
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.size()).isZero();
    }

    @Test
    void shouldAddFirst() {
        var list = new SinglyLinkedList<Integer>();
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
        var list = new SinglyLinkedList<String>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");

        assertThat(list.get(0)).isEqualTo("a");
        assertThat(list.get(1)).isEqualTo("b");
        assertThat(list.get(2)).isEqualTo("c");
    }

    @Test
    void shouldRemoveFirst() {
        var list = new SinglyLinkedList<Integer>();
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);

        assertThat(list.removeFirst()).isEqualTo(10);
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(20);
    }

    @Test
    void shouldRemoveLast() {
        var list = new SinglyLinkedList<Integer>();
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);

        assertThat(list.removeLast()).isEqualTo(30);
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(1)).isEqualTo(20);
    }

    @Test
    void shouldRemoveLastSingleElement() {
        var list = new SinglyLinkedList<Integer>();
        list.addFirst(42);

        assertThat(list.removeLast()).isEqualTo(42);
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    void shouldThrowOnRemoveFromEmpty() {
        var list = new SinglyLinkedList<Integer>();

        assertThatThrownBy(list::removeFirst)
                .isInstanceOf(NoSuchElementException.class);

        assertThatThrownBy(list::removeLast)
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldContainElement() {
        var list = new SinglyLinkedList<String>();
        list.addLast("hello");
        list.addLast("world");

        assertThat(list.contains("hello")).isTrue();
        assertThat(list.contains("world")).isTrue();
        assertThat(list.contains("foo")).isFalse();
    }

    @Test
    void shouldContainNull() {
        var list = new SinglyLinkedList<String>();
        list.addLast(null);
        list.addLast("a");

        assertThat(list.contains(null)).isTrue();
    }

    @Test
    void shouldReverse() {
        var list = new SinglyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addLast(4);

        list.reverse();

        assertThat(list.get(0)).isEqualTo(4);
        assertThat(list.get(1)).isEqualTo(3);
        assertThat(list.get(2)).isEqualTo(2);
        assertThat(list.get(3)).isEqualTo(1);
    }

    @Test
    void shouldReverseEmptyList() {
        var list = new SinglyLinkedList<Integer>();
        list.reverse();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    void shouldReverseSingleElement() {
        var list = new SinglyLinkedList<Integer>();
        list.addFirst(1);
        list.reverse();
        assertThat(list.get(0)).isEqualTo(1);
    }

    @Test
    void shouldFindMiddleOddLength() {
        var list = new SinglyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addLast(4);
        list.addLast(5);

        assertThat(list.findMiddle()).isEqualTo(3);
    }

    @Test
    void shouldFindMiddleEvenLength() {
        var list = new SinglyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addLast(4);

        assertThat(list.findMiddle()).isEqualTo(3);
    }

    @Test
    void shouldThrowFindMiddleOnEmpty() {
        var list = new SinglyLinkedList<Integer>();
        assertThatThrownBy(list::findMiddle)
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldDetectNoCycle() {
        var list = new SinglyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        assertThat(list.hasCycle()).isFalse();
    }

    @Test
    void shouldDetectCycle() {
        var list = new SinglyLinkedList<Integer>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addLast(4);

        // Create cycle: 4 -> 2
        var node = list.getHead();
        var secondNode = node.getNext();
        var lastNode = secondNode.getNext().getNext();
        lastNode.setNext(secondNode);

        assertThat(list.hasCycle()).isTrue();
    }

    @Test
    void shouldThrowOnInvalidIndex() {
        var list = new SinglyLinkedList<Integer>();
        list.addFirst(1);

        assertThatThrownBy(() -> list.get(5))
                .isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.get(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }
}
