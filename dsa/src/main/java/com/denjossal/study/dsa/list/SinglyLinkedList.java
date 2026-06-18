package com.denjossal.study.dsa.list;

import java.util.NoSuchElementException;

/**
 * Singly linked list with O(1) head insert, O(n) tail insert, and fast/slow pointer utilities.
 *
 * Complexities:
 *   addFirst:    O(1)
 *   addLast:     O(n) — no tail pointer kept intentionally, to practice traversal
 *   removeFirst: O(1)
 *   get(index):  O(n)
 *   reverse:     O(n) time, O(1) space
 *   findMiddle:  O(n) — fast/slow pointer technique
 *   hasCycle:    O(n) — Floyd's cycle detection
 */
public class SinglyLinkedList<T> {

    private Node<T> head;
    private int size;

    public void addFirst(T value) {
        head = new Node<>(value, head);
        size++;
    }

    public void addLast(T value) {
        Node<T> newNode = new Node<>(value, null);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        T value = head.value;
        head = head.next;
        size--;
        return value;
    }

    public T removeLast() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        if (head.next == null) {
            T value = head.value;
            head = null;
            size--;
            return value;
        }
        Node<T> current = head;
        while (current.next.next != null) {
            current = current.next;
        }
        T value = current.next.value;
        current.next = null;
        size--;
        return value;
    }

    public T get(int index) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    public boolean contains(T value) {
        Node<T> current = head;
        while (current != null) {
            if (current.value == null ? value == null : current.value.equals(value)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Reverses the list in place. O(n) time, O(1) space.
     */
    public void reverse() {
        Node<T> prev = null;
        Node<T> current = head;
        while (current != null) {
            Node<T> next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        head = prev;
    }

    /**
     * Finds the middle element using the fast/slow pointer technique.
     * For even-length lists, returns the second of the two middle elements.
     */
    public T findMiddle() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        Node<T> slow = head;
        Node<T> fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow.value;
    }

    /**
     * Detects a cycle using Floyd's tortoise and hare algorithm.
     * Note: a cycle can only be created externally via getHead() for testing.
     */
    public boolean hasCycle() {
        Node<T> slow = head;
        Node<T> fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Node<T> getHead() {
        return head;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    public static class Node<T> {
        T value;
        Node<T> next;

        public Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public T getValue() {
            return value;
        }
    }
}
