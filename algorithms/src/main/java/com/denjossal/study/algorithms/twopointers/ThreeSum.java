package com.denjossal.study.algorithms.twopointers;

import java.util.*;

/**
 * LeetCode 15 — 3Sum (Medium)
 * Pattern: Two Pointers (sort + fix one, sweep the rest)
 * Time: O(n^2)
 * Space: O(1) extra (excluding output)
 */
public class ThreeSum {

    public List<List<Integer>> solve(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            if (nums[i] > 0) break;

            int left = i + 1, right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum < 0) {
                    left++;
                } else if (sum > 0) {
                    right--;
                } else {
                    result.add(List.of(nums[i], nums[left], nums[right]));
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) left++;
                    while (left < right && nums[right] == nums[right + 1]) right--;
                }
            }
        }
        return result;
    }
}
