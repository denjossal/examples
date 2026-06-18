package com.denjossal.study.algorithms.arrays;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TopKFrequentTest {

    private final TopKFrequent solution = new TopKFrequent();

    @Test
    void shouldFindTopKFrequent() {
        int[] result = solution.solve(new int[] {1, 1, 1, 2, 2, 3}, 2);
        assertThat(result).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void shouldHandleSingleElement() {
        int[] result = solution.solve(new int[] {1}, 1);
        assertThat(result).containsExactly(1);
    }

    @Test
    void shouldWorkWithBucketSort() {
        int[] result = solution.solveBucketSort(new int[] {1, 1, 1, 2, 2, 3}, 2);
        assertThat(result).containsExactlyInAnyOrder(1, 2);
    }
}
