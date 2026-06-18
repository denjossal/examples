package com.denjossal.study.dsa.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Binary Search Tree with insert, search, delete, and all traversal orders.
 *
 * Complexities (balanced):
 *   insert/search/delete: O(log n)
 * Worst case (degenerate / sorted input): O(n)
 *
 * Traversals:
 *   inOrder:    left → root → right  (produces sorted output)
 *   preOrder:   root → left → right  (useful for serialization)
 *   postOrder:  left → right → root  (useful for deletion)
 *   levelOrder: BFS level by level
 */
public class BinarySearchTree<T extends Comparable<T>> {

    private Node<T> root;
    private int size;

    public void insert(T value) {
        root = insertRec(root, value);
        size++;
    }

    public boolean contains(T value) {
        return searchRec(root, value) != null;
    }

    public void delete(T value) {
        if (contains(value)) {
            root = deleteRec(root, value);
            size--;
        }
    }

    public T min() {
        if (root == null) return null;
        return findMin(root).value;
    }

    public T max() {
        if (root == null) return null;
        Node<T> current = root;
        while (current.right != null) current = current.right;
        return current.value;
    }

    public int height() {
        return heightRec(root);
    }

    public List<T> inOrder() {
        List<T> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    public List<T> preOrder() {
        List<T> result = new ArrayList<>();
        preOrderRec(root, result);
        return result;
    }

    public List<T> postOrder() {
        List<T> result = new ArrayList<>();
        postOrderRec(root, result);
        return result;
    }

    public List<T> levelOrder() {
        List<T> result = new ArrayList<>();
        if (root == null) return result;

        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node<T> current = queue.poll();
            result.add(current.value);
            if (current.left != null) queue.add(current.left);
            if (current.right != null) queue.add(current.right);
        }

        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private Node<T> insertRec(Node<T> node, T value) {
        if (node == null) return new Node<>(value);

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = insertRec(node.left, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, value);
        }
        // duplicate: ignore
        else {
            size--;
        }
        return node;
    }

    private Node<T> searchRec(Node<T> node, T value) {
        if (node == null) return null;

        int cmp = value.compareTo(node.value);
        if (cmp < 0) return searchRec(node.left, value);
        if (cmp > 0) return searchRec(node.right, value);
        return node;
    }

    private Node<T> deleteRec(Node<T> node, T value) {
        if (node == null) return null;

        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = deleteRec(node.left, value);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, value);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            Node<T> successor = findMin(node.right);
            node.value = successor.value;
            node.right = deleteRec(node.right, successor.value);
        }
        return node;
    }

    private Node<T> findMin(Node<T> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private int heightRec(Node<T> node) {
        if (node == null) return -1;
        return 1 + Math.max(heightRec(node.left), heightRec(node.right));
    }

    private void inOrderRec(Node<T> node, List<T> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.value);
        inOrderRec(node.right, result);
    }

    private void preOrderRec(Node<T> node, List<T> result) {
        if (node == null) return;
        result.add(node.value);
        preOrderRec(node.left, result);
        preOrderRec(node.right, result);
    }

    private void postOrderRec(Node<T> node, List<T> result) {
        if (node == null) return;
        postOrderRec(node.left, result);
        postOrderRec(node.right, result);
        result.add(node.value);
    }

    private static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
        }
    }
}
