package com.denjossal.study.algorithms.trees;

/**
 * LeetCode 236 — Lowest Common Ancestor of a Binary Tree (Medium)
 * Pattern: Trees DFS (post-order, bubble up non-null results)
 * Time: O(n)
 * Space: O(h)
 */
public class LowestCommonAncestor {

    public static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    public TreeNode solve(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;

        TreeNode left = solve(root.left, p, q);
        TreeNode right = solve(root.right, p, q);

        if (left != null && right != null) return root;
        return left != null ? left : right;
    }
}
