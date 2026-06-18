package com.denjossal.study.algorithms.twopointers;

/**
 * LeetCode 11 — Container With Most Water (Medium)
 * Pattern: Two Pointers (greedy narrowing)
 * Time: O(n)
 * Space: O(1)
 */
public class ContainerWithMostWater {

    public int solve(int[] height) {
        int left = 0, right = height.length - 1;
        int maxArea = 0;

        while (left < right) {
            int area = Math.min(height[left], height[right]) * (right - left);
            maxArea = Math.max(maxArea, area);

            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return maxArea;
    }
}
