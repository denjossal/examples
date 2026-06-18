package com.denjossal.study.algorithms.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 39 — Combination Sum (Medium)
 * Pattern: Backtracking (unbounded choices, prune when sum exceeds target)
 * Time: O(n^(target/min))
 * Space: O(target/min) recursion depth
 */
public class CombinationSum {

    public List<List<Integer>> solve(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int remaining, int start,
                           List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (remaining < 0) return;

        for (int i = start; i < candidates.length; i++) {
            current.add(candidates[i]);
            backtrack(candidates, remaining - candidates[i], i, current, result);
            current.remove(current.size() - 1);
        }
    }
}
