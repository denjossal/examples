package com.denjossal.study.algorithms.arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ProductExceptSelfTest {

    private final ProductExceptSelf solution = new ProductExceptSelf();

    @Test
    void shouldComputeProducts() {
        assertThat(solution.solve(new int[]{1, 2, 3, 4}))
                .containsExactly(24, 12, 8, 6);
    }

    @Test
    void shouldHandleZero() {
        assertThat(solution.solve(new int[]{-1, 1, 0, -3, 3}))
                .containsExactly(0, 0, 9, 0, 0);
    }

    @Test
    void shouldHandleTwoElements() {
        assertThat(solution.solve(new int[]{3, 5}))
                .containsExactly(5, 3);
    }
}
