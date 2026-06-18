package com.denjossal.study.algorithms.backtracking;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SubsetsTest {

    private final Subsets solution = new Subsets();

    @Test
    void shouldGenerateAllSubsets() {
        var result = solution.solve(new int[] {1, 2, 3});
        assertThat(result).hasSize(8); // 2^3
        assertThat(result).contains(List.of());
        assertThat(result).contains(List.of(1, 2, 3));
        assertThat(result).contains(List.of(1, 3));
    }

    @Test
    void shouldHandleSingleElement() {
        var result = solution.solve(new int[] {0});
        assertThat(result).hasSize(2);
        assertThat(result).contains(List.of());
        assertThat(result).contains(List.of(0));
    }
}
