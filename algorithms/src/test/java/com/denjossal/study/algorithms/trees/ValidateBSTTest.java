package com.denjossal.study.algorithms.trees;

import com.denjossal.study.algorithms.trees.ValidateBST.TreeNode;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ValidateBSTTest {

    private final ValidateBST solution = new ValidateBST();

    @Test
    void shouldValidateCorrectBST() {
        TreeNode root = new TreeNode(2);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);

        assertThat(solution.solve(root)).isTrue();
    }

    @Test
    void shouldRejectInvalidBST() {
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(1);
        root.right = new TreeNode(4);
        root.right.left = new TreeNode(3);
        root.right.right = new TreeNode(6);

        assertThat(solution.solve(root)).isFalse();
    }

    @Test
    void shouldHandleNull() {
        assertThat(solution.solve(null)).isTrue();
    }
}
