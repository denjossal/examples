package com.denjossal.study.algorithms.trees;

/**
 * LeetCode 98 — Validate Binary Search Tree (Medium)
 * Pattern: Trees DFS (pass valid range down)
 * Time: O(n)
 * Space: O(h) — recursion stack
 */
public class ValidateBST {

    public static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    public boolean solve(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean validate(TreeNode node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;
        return validate(node.left, min, node.val)
                && validate(node.right, node.val, max);
    }
}
