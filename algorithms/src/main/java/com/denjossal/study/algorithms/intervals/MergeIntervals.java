package com.denjossal.study.algorithms.intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 56 — Merge Intervals (Medium)
 * Pattern: Intervals (sort by start, merge overlapping)
 * Time: O(n log n)
 * Space: O(n)
 */
public class MergeIntervals {

    public int[][] solve(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];
        merged.add(current);

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= current[1]) {
                current[1] = Math.max(current[1], intervals[i][1]);
            } else {
                current = intervals[i];
                merged.add(current);
            }
        }
        return merged.toArray(new int[0][]);
    }
}
