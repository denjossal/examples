package com.denjossal.study.algorithms.heap;

import java.util.PriorityQueue;

/**
 * LeetCode 973 — K Closest Points to Origin (Medium)
 * Pattern: Heap / Top-K (max-heap of size k)
 * Time: O(n log k)
 * Space: O(k)
 */
public class KClosestPoints {

    public int[][] solve(int[][] points, int k) {
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
                (a, b) -> dist(b) - dist(a)
        );

        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > k) maxHeap.poll();
        }

        return maxHeap.toArray(new int[k][]);
    }

    private int dist(int[] point) {
        return point[0] * point[0] + point[1] * point[1];
    }
}
