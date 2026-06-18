package com.denjossal.study.dsa.list;

import java.util.Arrays;

/**
 * A resizable array implementation (like ArrayList) built from scratch.
 * Demonstrates amortized O(1) append via doubling strategy.
 */
public class DynamicArray<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] data;
    private int size;

    public DynamicArray() {
        this.data = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative: " + initialCapacity);
        }
        this.data = new Object[initialCapacity];
        this.size = 0;
    }

    public void add(T element) {
        ensureCapacity();
        data[size++] = element;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    public void set(int index, T element) {
        checkIndex(index);
        data[index] = element;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }
        data[--size] = null;
        return removed;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return data.length;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            int newCapacity = data.length == 0 ? DEFAULT_CAPACITY : data.length * 2;
            data = Arrays.copyOf(data, newCapacity);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
