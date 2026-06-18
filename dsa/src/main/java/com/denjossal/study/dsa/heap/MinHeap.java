package com.denjossal.study.dsa.heap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Binary min-heap backed by a resizable array (like PriorityQueue).
 *
 * Parent of i: (i-1)/2
 * Left child:  2*i + 1
 * Right child: 2*i + 2
 *
 * Complexities:
 *   insert (offer): O(log n)
 *   poll (extract min): O(log n)
 *   peek: O(1)
 *   heapify (build from array): O(n)
 */
public class MinHeap<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] data;
    private int size;
    private final Comparator<T> comparator;

    @SuppressWarnings("unchecked")
    public MinHeap() {
        this((Comparator<T>) Comparator.naturalOrder());
    }

    public MinHeap(Comparator<T> comparator) {
        this.data = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
    }

    public MinHeap(T[] elements) {
        this(elements, null);
    }

    @SuppressWarnings("unchecked")
    public MinHeap(T[] elements, Comparator<T> comparator) {
        this.comparator = comparator != null ? comparator : (Comparator<T>) Comparator.naturalOrder();
        this.data = Arrays.copyOf(elements, Math.max(elements.length, DEFAULT_CAPACITY), Object[].class);
        this.size = elements.length;
        heapify();
    }

    public void offer(T element) {
        if (size == data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
        data[size] = element;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        T min = (T) data[0];
        data[0] = data[--size];
        data[size] = null;
        if (size > 0) siftDown(0);
        return min;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return (T) data[0];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare((T) data[index], (T) data[parent]) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && comparator.compare((T) data[left], (T) data[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && comparator.compare((T) data[right], (T) data[smallest]) < 0) {
                smallest = right;
            }
            if (smallest == index) break;
            swap(index, smallest);
            index = smallest;
        }
    }

    private void heapify() {
        for (int i = (size / 2) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    private void swap(int i, int j) {
        Object temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
}
