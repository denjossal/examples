package com.denjossal.study.algorithms.binarysearch;

/**
 * LeetCode 153 — Find Minimum in Rotated Sorted Array (Medium)
 * Pattern: Binary Search (converge on the inflection point)
 * Time: O(log n)
 * Space: O(1)
 */
public class FindMinRotatedSorted {

    public int solve(int[] nums) {
        int lo = 0, hi = nums.length - 1;

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return nums[lo];
    }
}
