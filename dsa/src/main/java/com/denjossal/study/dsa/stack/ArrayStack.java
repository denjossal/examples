package com.denjossal.study.dsa.stack;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Stack implemented with a resizable array (like ArrayDeque used as a stack).
 *
 * Complexities:
 *   push: amortized O(1)
 *   pop:  O(1)
 *   peek: O(1)
 */
public class ArrayStack<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] data;
    private int size;

    public ArrayStack() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public void push(T element) {
        if (size == data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
        data[size++] = element;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T element = (T) data[--size];
        data[size] = null;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) data[size - 1];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
