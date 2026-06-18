package com.denjossal.study.algorithms.binarysearch;

/**
 * LeetCode 33 — Search in Rotated Sorted Array (Medium)
 * Pattern: Binary Search (determine which half is sorted)
 * Time: O(log n)
 * Space: O(1)
 */
public class SearchRotatedSortedArray {

    public int solve(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;

            if (nums[mid] == target) return mid;

            if (nums[lo] <= nums[mid]) {
                if (target >= nums[lo] && target < nums[mid]) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
            } else {
                if (target > nums[mid] && target <= nums[hi]) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return -1;
    }
}
