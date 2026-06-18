package com.denjossal.study.algorithms.binarysearch;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class FindMinRotatedSortedTest {

    private final FindMinRotatedSorted solution = new FindMinRotatedSorted();

    @Test
    void shouldFindMin() {
        assertThat(solution.solve(new int[]{3, 4, 5, 1, 2})).isEqualTo(1);
    }

    @Test
    void shouldFindMinAlreadySorted() {
        assertThat(solution.solve(new int[]{1, 2, 3, 4, 5})).isEqualTo(1);
    }

    @Test
    void shouldHandleTwoElements() {
        assertThat(solution.solve(new int[]{2, 1})).isEqualTo(1);
    }
}
