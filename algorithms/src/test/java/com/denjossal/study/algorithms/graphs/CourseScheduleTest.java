package com.denjossal.study.algorithms.graphs;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CourseScheduleTest {

    private final CourseSchedule solution = new CourseSchedule();

    @Test
    void shouldFinishWithNoCycle() {
        assertThat(solution.canFinish(2, new int[][] {{1, 0}})).isTrue();
    }

    @Test
    void shouldNotFinishWithCycle() {
        assertThat(solution.canFinish(2, new int[][] {{1, 0}, {0, 1}})).isFalse();
    }

    @Test
    void shouldHandleNoPrerequisites() {
        assertThat(solution.canFinish(3, new int[][] {})).isTrue();
    }

    @Test
    void shouldHandleComplexDAG() {
        assertThat(solution.canFinish(4, new int[][] {{1, 0}, {2, 0}, {3, 1}, {3, 2}}))
                .isTrue();
    }
}
