package com.denjossal.study.algorithms.greedy;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JumpGameTest {

    private final JumpGame solution = new JumpGame();

    @Test
    void shouldReachEnd() {
        assertThat(solution.solve(new int[] {2, 3, 1, 1, 4})).isTrue();
    }

    @Test
    void shouldNotReachEnd() {
        assertThat(solution.solve(new int[] {3, 2, 1, 0, 4})).isFalse();
    }

    @Test
    void shouldHandleSingle() {
        assertThat(solution.solve(new int[] {0})).isTrue();
    }
}
