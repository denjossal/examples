package com.denjossal.study.algorithms.intervals;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MergeIntervalsTest {

    private final MergeIntervals solution = new MergeIntervals();

    @Test
    void shouldMergeOverlapping() {
        int[][] result = solution.solve(new int[][] {{1, 3}, {2, 6}, {8, 10}, {15, 18}});
        assertThat(result).isDeepEqualTo(new int[][] {{1, 6}, {8, 10}, {15, 18}});
    }

    @Test
    void shouldMergeAdjacent() {
        int[][] result = solution.solve(new int[][] {{1, 4}, {4, 5}});
        assertThat(result).isDeepEqualTo(new int[][] {{1, 5}});
    }

    @Test
    void shouldHandleSingleInterval() {
        int[][] result = solution.solve(new int[][] {{1, 1}});
        assertThat(result).isDeepEqualTo(new int[][] {{1, 1}});
    }
}
