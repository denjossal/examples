package com.denjossal.study.algorithms.graphs;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class NumberOfIslandsTest {

    private final NumberOfIslands solution = new NumberOfIslands();

    @Test
    void shouldCountIslands() {
        char[][] grid = {
                {'1', '1', '1', '1', '0'},
                {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '0', '0', '0'}
        };
        assertThat(solution.solve(grid)).isEqualTo(1);
    }

    @Test
    void shouldCountMultipleIslands() {
        char[][] grid = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };
        assertThat(solution.solve(grid)).isEqualTo(3);
    }

    @Test
    void shouldHandleEmpty() {
        assertThat(solution.solve(new char[][]{})).isEqualTo(0);
    }
}
