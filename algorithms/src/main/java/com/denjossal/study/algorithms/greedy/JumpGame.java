package com.denjossal.study.algorithms.greedy;

/**
 * LeetCode 55 — Jump Game (Medium)
 * Pattern: Greedy (track farthest reachable index)
 * Time: O(n)
 * Space: O(1)
 */
public class JumpGame {

    public boolean solve(int[] nums) {
        int farthest = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) return false;
            farthest = Math.max(farthest, i + nums[i]);
        }
        return true;
    }
}
