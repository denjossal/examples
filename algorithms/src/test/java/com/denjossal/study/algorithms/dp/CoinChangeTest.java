package com.denjossal.study.algorithms.dp;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CoinChangeTest {

    private final CoinChange solution = new CoinChange();

    @Test
    void shouldFindMinCoins() {
        assertThat(solution.solve(new int[] {1, 5, 10, 25}, 30)).isEqualTo(2);
    }

    @Test
    void shouldReturnMinusOneWhenImpossible() {
        assertThat(solution.solve(new int[] {2}, 3)).isEqualTo(-1);
    }

    @Test
    void shouldHandleZeroAmount() {
        assertThat(solution.solve(new int[] {1}, 0)).isEqualTo(0);
    }

    @Test
    void shouldHandleStandardCase() {
        assertThat(solution.solve(new int[] {1, 2, 5}, 11)).isEqualTo(3);
    }
}
