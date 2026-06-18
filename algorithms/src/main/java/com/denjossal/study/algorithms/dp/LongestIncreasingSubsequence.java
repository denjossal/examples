package com.denjossal.study.algorithms.dp;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 300 — Longest Increasing Subsequence (Medium)
 * Pattern: 1-D DP + Binary Search (patience sorting)
 * Time: O(n log n) with binary search approach
 * Space: O(n)
 */
public class LongestIncreasingSubsequence {

    /**
     * O(n^2) DP approach — clearer for interviews.
     */
    public int solveDP(int[] nums) {
        if (nums.length == 0) return 0;
        int[] dp = new int[nums.length];
        int maxLen = 1;

        for (int i = 0; i < nums.length; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLen = Math.max(maxLen, dp[i]);
        }
        return maxLen;
    }

    /**
     * O(n log n) patience sorting approach.
     */
    public int solve(int[] nums) {
        List<Integer> tails = new ArrayList<>();

        for (int num : nums) {
            int pos = lowerBound(tails, num);
            if (pos == tails.size()) {
                tails.add(num);
            } else {
                tails.set(pos, num);
            }
        }
        return tails.size();
    }

    private int lowerBound(List<Integer> tails, int target) {
        int lo = 0, hi = tails.size();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (tails.get(mid) < target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
