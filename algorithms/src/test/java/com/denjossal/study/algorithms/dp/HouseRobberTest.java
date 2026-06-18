package com.denjossal.study.algorithms.dp;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HouseRobberTest {

    private final HouseRobber solution = new HouseRobber();

    @Test
    void shouldMaximizeRobbery() {
        assertThat(solution.solve(new int[] {1, 2, 3, 1})).isEqualTo(4);
    }

    @Test
    void shouldHandleLargerCase() {
        assertThat(solution.solve(new int[] {2, 7, 9, 3, 1})).isEqualTo(12);
    }

    @Test
    void shouldHandleSingleHouse() {
        assertThat(solution.solve(new int[] {5})).isEqualTo(5);
    }

    @Test
    void shouldHandleEmpty() {
        assertThat(solution.solve(new int[] {})).isEqualTo(0);
    }
}
