package com.denjossal.study.algorithms.trees;

import java.util.*;

/**
 * LeetCode 102 — Binary Tree Level Order Traversal (Medium)
 * Pattern: Trees BFS (queue with level grouping)
 * Time: O(n)
 * Space: O(n)
 */
public class LevelOrderTraversal {

    public List<List<Integer>> solve(int[] tree) {
        List<List<Integer>> result = new ArrayList<>();
        if (tree == null || tree.length == 0 || tree[0] == -1) return result;

        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                int idx = queue.poll();
                if (idx >= tree.length || tree[idx] == -1) continue;

                level.add(tree[idx]);
                int left = 2 * idx + 1;
                int right = 2 * idx + 2;
                if (left < tree.length && tree[left] != -1) queue.add(left);
                if (right < tree.length && tree[right] != -1) queue.add(right);
            }
            if (!level.isEmpty()) result.add(level);
        }
        return result;
    }

    public static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    public List<List<Integer>> solve(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            result.add(level);
        }
        return result;
    }
}
