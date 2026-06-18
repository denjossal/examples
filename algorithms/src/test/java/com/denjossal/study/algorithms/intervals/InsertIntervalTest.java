package com.denjossal.study.algorithms.intervals;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InsertIntervalTest {

    private final InsertInterval solution = new InsertInterval();

    @Test
    void shouldInsertAndMerge() {
        int[][] result = solution.solve(new int[][] {{1, 3}, {6, 9}}, new int[] {2, 5});
        assertThat(result).isDeepEqualTo(new int[][] {{1, 5}, {6, 9}});
    }

    @Test
    void shouldInsertOverlappingMultiple() {
        int[][] result = solution.solve(new int[][] {{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}}, new int[] {4, 8});
        assertThat(result).isDeepEqualTo(new int[][] {{1, 2}, {3, 10}, {12, 16}});
    }
}
