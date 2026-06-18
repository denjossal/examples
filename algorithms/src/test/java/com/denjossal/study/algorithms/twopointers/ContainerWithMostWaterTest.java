package com.denjossal.study.algorithms.twopointers;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ContainerWithMostWaterTest {

    private final ContainerWithMostWater solution = new ContainerWithMostWater();

    @Test
    void shouldFindMaxArea() {
        assertThat(solution.solve(new int[] {1, 8, 6, 2, 5, 4, 8, 3, 7})).isEqualTo(49);
    }

    @Test
    void shouldHandleTwoElements() {
        assertThat(solution.solve(new int[] {1, 1})).isEqualTo(1);
    }
}
