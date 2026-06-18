package com.denjossal.study.algorithms.twopointers;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class ThreeSumTest {

    private final ThreeSum solution = new ThreeSum();

    @Test
    void shouldFindTriplets() {
        var result = solution.solve(new int[]{-1, 0, 1, 2, -1, -4});
        assertThat(result).hasSize(2);
        assertThat(result).contains(List.of(-1, -1, 2));
        assertThat(result).contains(List.of(-1, 0, 1));
    }

    @Test
    void shouldReturnEmptyWhenNoTriplets() {
        assertThat(solution.solve(new int[]{0, 1, 1})).isEmpty();
    }

    @Test
    void shouldHandleAllZeros() {
        var result = solution.solve(new int[]{0, 0, 0});
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsExactly(0, 0, 0);
    }
}
