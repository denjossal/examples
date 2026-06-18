package com.denjossal.study.algorithms.stack;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LeetCode 739 — Daily Temperatures (Medium)
 * Pattern: Monotonic Stack (decreasing stack of indices)
 * Time: O(n)
 * Space: O(n)
 */
public class DailyTemperatures {

    public int[] solve(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prev = stack.pop();
                result[prev] = i - prev;
            }
            stack.push(i);
        }
        return result;
    }
}
