package com.denjossal.study.algorithms.greedy;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MaxSubarrayTest {

    private final MaxSubarray solution = new MaxSubarray();

    @Test
    void shouldFindMaxSubarray() {
        assertThat(solution.solve(new int[] {-2, 1, -3, 4, -1, 2, 1, -5, 4})).isEqualTo(6);
    }

    @Test
    void shouldHandleAllNegative() {
        assertThat(solution.solve(new int[] {-3, -2, -1})).isEqualTo(-1);
    }

    @Test
    void shouldHandleSingle() {
        assertThat(solution.solve(new int[] {1})).isEqualTo(1);
    }
}
