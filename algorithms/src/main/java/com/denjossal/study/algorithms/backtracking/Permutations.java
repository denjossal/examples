package com.denjossal.study.algorithms.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 46 — Permutations (Medium)
 * Pattern: Backtracking (swap or used-array approach)
 * Time: O(n * n!)
 * Space: O(n)
 */
public class Permutations {

    public List<List<Integer>> solve(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            current.add(nums[i]);
            backtrack(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
}
