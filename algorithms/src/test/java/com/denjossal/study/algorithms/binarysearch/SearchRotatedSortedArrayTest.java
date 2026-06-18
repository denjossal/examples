package com.denjossal.study.algorithms.binarysearch;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class SearchRotatedSortedArrayTest {

    private final SearchRotatedSortedArray solution = new SearchRotatedSortedArray();

    @Test
    void shouldFindTarget() {
        assertThat(solution.solve(new int[]{4, 5, 6, 7, 0, 1, 2}, 0)).isEqualTo(4);
    }

    @Test
    void shouldReturnMinusOneWhenNotFound() {
        assertThat(solution.solve(new int[]{4, 5, 6, 7, 0, 1, 2}, 3)).isEqualTo(-1);
    }

    @Test
    void shouldHandleSingleElement() {
        assertThat(solution.solve(new int[]{1}, 1)).isEqualTo(0);
        assertThat(solution.solve(new int[]{1}, 0)).isEqualTo(-1);
    }
}
