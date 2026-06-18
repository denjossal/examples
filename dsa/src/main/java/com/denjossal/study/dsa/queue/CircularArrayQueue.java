package com.denjossal.study.dsa.queue;

import java.util.NoSuchElementException;

/**
 * Queue implemented with a circular array (like ArrayDeque used as a queue).
 * Avoids shifting elements on dequeue by using head/tail pointers with wraparound.
 *
 * Complexities:
 *   enqueue: amortized O(1)
 *   dequeue: O(1)
 *   peek:    O(1)
 */
public class CircularArrayQueue<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] data;
    private int head;
    private int tail;
    private int size;

    public CircularArrayQueue() {
        data = new Object[DEFAULT_CAPACITY];
        head = 0;
        tail = 0;
        size = 0;
    }

    public void enqueue(T element) {
        if (size == data.length) {
            resize();
        }
        data[tail] = element;
        tail = (tail + 1) % data.length;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        T element = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        size--;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        return (T) data[head];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize() {
        Object[] newData = new Object[data.length * 2];
        for (int i = 0; i < size; i++) {
            newData[i] = data[(head + i) % data.length];
        }
        data = newData;
        head = 0;
        tail = size;
    }
}
