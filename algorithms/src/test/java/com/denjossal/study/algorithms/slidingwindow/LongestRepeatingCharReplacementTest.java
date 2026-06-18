package com.denjossal.study.algorithms.slidingwindow;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class LongestRepeatingCharReplacementTest {

    private final LongestRepeatingCharReplacement solution = new LongestRepeatingCharReplacement();

    @Test
    void shouldFindLongest() {
        assertThat(solution.solve("ABAB", 2)).isEqualTo(4);
    }

    @Test
    void shouldFindLongestWithLimit() {
        assertThat(solution.solve("AABABBA", 1)).isEqualTo(4);
    }
}
