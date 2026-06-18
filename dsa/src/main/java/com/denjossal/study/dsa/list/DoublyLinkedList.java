package com.denjossal.study.dsa.list;

import java.util.NoSuchElementException;

/**
 * Doubly linked list with O(1) head/tail insert and remove.
 * Uses sentinel nodes to simplify edge cases.
 *
 * Complexities:
 *   addFirst/addLast:       O(1)
 *   removeFirst/removeLast: O(1)
 *   get(index):             O(n)
 *   remove(index):          O(n)
 */
public class DoublyLinkedList<T> {

    private final Node<T> sentinel;
    private int size;

    public DoublyLinkedList() {
        sentinel = new Node<>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(T value) {
        insertAfter(sentinel, value);
    }

    public void addLast(T value) {
        insertAfter(sentinel.prev, value);
    }

    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("List is empty");
        return unlink(sentinel.next);
    }

    public T removeLast() {
        if (isEmpty()) throw new NoSuchElementException("List is empty");
        return unlink(sentinel.prev);
    }

    public T get(int index) {
        return nodeAt(index).value;
    }

    public void set(int index, T value) {
        nodeAt(index).value = value;
    }

    public T remove(int index) {
        return unlink(nodeAt(index));
    }

    public boolean contains(T value) {
        Node<T> current = sentinel.next;
        while (current != sentinel) {
            if (current.value == null ? value == null : current.value.equals(value)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void insertAfter(Node<T> node, T value) {
        Node<T> newNode = new Node<>(value, node, node.next);
        node.next.prev = newNode;
        node.next = newNode;
        size++;
    }

    private T unlink(Node<T> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
        return node.value;
    }

    private Node<T> nodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node<T> current;
        if (index < size / 2) {
            current = sentinel.next;
            for (int i = 0; i < index; i++) current = current.next;
        } else {
            current = sentinel.prev;
            for (int i = size - 1; i > index; i--) current = current.prev;
        }
        return current;
    }

    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;

        Node(T value, Node<T> prev, Node<T> next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
}
