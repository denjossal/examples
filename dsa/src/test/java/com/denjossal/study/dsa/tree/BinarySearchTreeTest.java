package com.denjossal.study.dsa.tree;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BinarySearchTreeTest {

    private BinarySearchTree<Integer> bst;

    @BeforeEach
    void setUp() {
        bst = new BinarySearchTree<>();
        //        5
        //       / \
        //      3   7
        //     / \ / \
        //    1  4 6  9
        bst.insert(5);
        bst.insert(3);
        bst.insert(7);
        bst.insert(1);
        bst.insert(4);
        bst.insert(6);
        bst.insert(9);
    }

    @Test
    void shouldInsertAndContain() {
        assertThat(bst.contains(5)).isTrue();
        assertThat(bst.contains(1)).isTrue();
        assertThat(bst.contains(9)).isTrue();
        assertThat(bst.contains(99)).isFalse();
        assertThat(bst.size()).isEqualTo(7);
    }

    @Test
    void shouldIgnoreDuplicates() {
        bst.insert(5);
        bst.insert(3);

        assertThat(bst.size()).isEqualTo(7);
    }

    @Test
    void shouldFindMinAndMax() {
        assertThat(bst.min()).isEqualTo(1);
        assertThat(bst.max()).isEqualTo(9);
    }

    @Test
    void shouldReturnNullMinMaxOnEmpty() {
        var empty = new BinarySearchTree<Integer>();
        assertThat(empty.min()).isNull();
        assertThat(empty.max()).isNull();
    }

    @Test
    void shouldComputeHeight() {
        assertThat(bst.height()).isEqualTo(2);

        bst.insert(10);
        bst.insert(11);
        assertThat(bst.height()).isEqualTo(4);
    }

    @Test
    void shouldTraverseInOrder() {
        assertThat(bst.inOrder()).containsExactly(1, 3, 4, 5, 6, 7, 9);
    }

    @Test
    void shouldTraversePreOrder() {
        assertThat(bst.preOrder()).containsExactly(5, 3, 1, 4, 7, 6, 9);
    }

    @Test
    void shouldTraversePostOrder() {
        assertThat(bst.postOrder()).containsExactly(1, 4, 3, 6, 9, 7, 5);
    }

    @Test
    void shouldTraverseLevelOrder() {
        assertThat(bst.levelOrder()).containsExactly(5, 3, 7, 1, 4, 6, 9);
    }

    @Test
    void shouldDeleteLeafNode() {
        bst.delete(1);

        assertThat(bst.contains(1)).isFalse();
        assertThat(bst.size()).isEqualTo(6);
        assertThat(bst.inOrder()).containsExactly(3, 4, 5, 6, 7, 9);
    }

    @Test
    void shouldDeleteNodeWithOneChild() {
        // Setup: insert 8 as right child of 9, making 9 a node with one child
        bst.insert(8);
        //        5
        //       / \
        //      3   7
        //     / \ / \
        //    1  4 6  9
        //           /
        //          8
        bst.delete(9);

        assertThat(bst.contains(9)).isFalse();
        assertThat(bst.contains(8)).isTrue();
        assertThat(bst.inOrder()).containsExactly(1, 3, 4, 5, 6, 7, 8);
    }

    @Test
    void shouldDeleteNodeWithTwoChildren() {
        bst.delete(3);

        assertThat(bst.contains(3)).isFalse();
        assertThat(bst.size()).isEqualTo(6);
        // In-order successor (4) replaces 3
        assertThat(bst.contains(1)).isTrue();
        assertThat(bst.contains(4)).isTrue();
        assertThat(bst.inOrder()).containsExactly(1, 4, 5, 6, 7, 9);
    }

    @Test
    void shouldDeleteRoot() {
        bst.delete(5);

        assertThat(bst.contains(5)).isFalse();
        assertThat(bst.size()).isEqualTo(6);
        // In-order successor (6) replaces 5
        assertThat(bst.inOrder()).containsExactly(1, 3, 4, 6, 7, 9);
    }

    @Test
    void shouldHandleDeleteNonExistent() {
        bst.delete(99);
        assertThat(bst.size()).isEqualTo(7);
    }

    @Test
    void shouldWorkOnEmptyTree() {
        var empty = new BinarySearchTree<Integer>();
        assertThat(empty.isEmpty()).isTrue();
        assertThat(empty.inOrder()).isEmpty();
        assertThat(empty.levelOrder()).isEmpty();
        assertThat(empty.height()).isEqualTo(-1);
    }
}
