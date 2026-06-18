package com.denjossal.study.algorithms.heap;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class KClosestPointsTest {

    private final KClosestPoints solution = new KClosestPoints();

    @Test
    void shouldFindKClosest() {
        int[][] points = {{1, 3}, {-2, 2}};
        int[][] result = solution.solve(points, 1);
        assertThat(result.length).isEqualTo(1);
        assertThat(result[0]).containsExactly(-2, 2);
    }

    @Test
    void shouldFindMultipleClosest() {
        int[][] points = {{3, 3}, {5, -1}, {-2, 4}};
        int[][] result = solution.solve(points, 2);
        assertThat(result.length).isEqualTo(2);
    }
}
