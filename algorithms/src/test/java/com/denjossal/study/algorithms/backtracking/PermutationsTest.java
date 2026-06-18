package com.denjossal.study.algorithms.backtracking;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class PermutationsTest {

    private final Permutations solution = new Permutations();

    @Test
    void shouldGenerateAllPermutations() {
        var result = solution.solve(new int[] {1, 2, 3});
        assertThat(result).hasSize(6); // 3!
        assertThat(result).contains(List.of(1, 2, 3));
        assertThat(result).contains(List.of(3, 2, 1));
    }

    @Test
    void shouldHandleSingle() {
        var result = solution.solve(new int[] {1});
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsExactly(1);
    }
}
