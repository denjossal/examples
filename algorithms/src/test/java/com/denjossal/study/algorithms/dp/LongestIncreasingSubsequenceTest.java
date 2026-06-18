package com.denjossal.study.algorithms.dp;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class LongestIncreasingSubsequenceTest {

    private final LongestIncreasingSubsequence solution = new LongestIncreasingSubsequence();

    @Test
    void shouldFindLIS() {
        assertThat(solution.solve(new int[]{10, 9, 2, 5, 3, 7, 101, 18})).isEqualTo(4);
    }

    @Test
    void shouldFindLISDP() {
        assertThat(solution.solveDP(new int[]{10, 9, 2, 5, 3, 7, 101, 18})).isEqualTo(4);
    }

    @Test
    void shouldHandleIncreasing() {
        assertThat(solution.solve(new int[]{1, 2, 3, 4, 5})).isEqualTo(5);
    }

    @Test
    void shouldHandleDecreasing() {
        assertThat(solution.solve(new int[]{5, 4, 3, 2, 1})).isEqualTo(1);
    }
}
