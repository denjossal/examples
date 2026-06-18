package com.denjossal.study.algorithms.greedy;

/**
 * LeetCode 53 — Maximum Subarray / Kadane's Algorithm (Medium)
 * Pattern: Greedy (extend or restart the running sum)
 * Time: O(n)
 * Space: O(1)
 */
public class MaxSubarray {

    public int solve(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }
        return maxSum;
    }
}
