package com.denjossal.study.algorithms.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 78 — Subsets (Medium)
 * Pattern: Backtracking (include/exclude decision tree)
 * Time: O(n * 2^n)
 * Space: O(n) recursion depth
 */
public class Subsets {

    public List<List<Integer>> solve(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrack(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
