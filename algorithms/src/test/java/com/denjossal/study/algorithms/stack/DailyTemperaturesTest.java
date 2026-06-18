package com.denjossal.study.algorithms.stack;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DailyTemperaturesTest {

    private final DailyTemperatures solution = new DailyTemperatures();

    @Test
    void shouldFindDaysToWarmer() {
        assertThat(solution.solve(new int[]{73, 74, 75, 71, 69, 72, 76, 73}))
                .containsExactly(1, 1, 4, 2, 1, 1, 0, 0);
    }

    @Test
    void shouldHandleDecreasingTemps() {
        assertThat(solution.solve(new int[]{5, 4, 3, 2, 1}))
                .containsExactly(0, 0, 0, 0, 0);
    }

    @Test
    void shouldHandleConstant() {
        assertThat(solution.solve(new int[]{30, 30, 30}))
                .containsExactly(0, 0, 0);
    }
}
