package com.denjossal.study.algorithms.heap;

import java.util.PriorityQueue;

/**
 * LeetCode 703 — Kth Largest Element in a Stream (Easy/Medium pattern)
 * Pattern: Heap / Top-K (min-heap of size k)
 * Time: O(log k) per add
 * Space: O(k)
 */
public class KthLargest {

    private final PriorityQueue<Integer> minHeap;
    private final int k;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.minHeap = new PriorityQueue<>();
        for (int n : nums) add(n);
    }

    public int add(int val) {
        minHeap.offer(val);
        if (minHeap.size() > k) minHeap.poll();
        return minHeap.peek();
    }
}
