package com.denjossal.study.algorithms.dp;

/**
 * LeetCode 198 — House Robber (Medium)
 * Pattern: 1-D DP (rob or skip each house)
 * Time: O(n)
 * Space: O(1)
 */
public class HouseRobber {

    public int solve(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int prev2 = 0;
        int prev1 = 0;

        for (int num : nums) {
            int current = Math.max(prev1, prev2 + num);
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
