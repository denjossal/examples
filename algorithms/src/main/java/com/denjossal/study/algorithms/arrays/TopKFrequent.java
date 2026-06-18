package com.denjossal.study.algorithms.arrays;

import java.util.*;

/**
 * LeetCode 347 — Top K Frequent Elements (Medium)
 * Pattern: Arrays & Hashing + Heap
 * Time: O(n log k) with heap, or O(n) with bucket sort
 * Space: O(n)
 */
public class TopKFrequent {

    public int[] solve(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) {
            freq.merge(n, 1, Integer::sum);
        }

        Queue<Integer> heap = new PriorityQueue<>(Comparator.comparingInt(freq::get));
        for (int key : freq.keySet()) {
            heap.offer(key);
            if (heap.size() > k) heap.poll();
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = heap.poll();
        }
        return result;
    }

    /**
     * O(n) bucket sort approach — uses frequency as index.
     */
    @SuppressWarnings("unchecked")
    public int[] solveBucketSort(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) {
            freq.merge(n, 1, Integer::sum);
        }

        List<Integer>[] buckets = new List[nums.length + 1];
        for (var entry : freq.entrySet()) {
            int f = entry.getValue();
            if (buckets[f] == null) buckets[f] = new ArrayList<>();
            buckets[f].add(entry.getKey());
        }

        int[] result = new int[k];
        int idx = 0;
        for (int i = buckets.length - 1; i >= 0 && idx < k; i--) {
            if (buckets[i] != null) {
                for (int val : buckets[i]) {
                    result[idx++] = val;
                    if (idx == k) break;
                }
            }
        }
        return result;
    }
}
