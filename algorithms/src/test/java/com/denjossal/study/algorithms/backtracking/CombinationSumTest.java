package com.denjossal.study.algorithms.backtracking;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class CombinationSumTest {

    private final CombinationSum solution = new CombinationSum();

    @Test
    void shouldFindCombinations() {
        var result = solution.solve(new int[] {2, 3, 6, 7}, 7);
        assertThat(result).hasSize(2);
        assertThat(result).contains(List.of(2, 2, 3));
        assertThat(result).contains(List.of(7));
    }

    @Test
    void shouldHandleNoSolution() {
        var result = solution.solve(new int[] {2}, 3);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldAllowReuse() {
        var result = solution.solve(new int[] {2, 3, 5}, 8);
        assertThat(result).contains(List.of(2, 2, 2, 2));
        assertThat(result).contains(List.of(2, 3, 3));
        assertThat(result).contains(List.of(3, 5));
    }
}
