package com.denjossal.study.algorithms.slidingwindow;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LongestSubstringWithoutRepeatingTest {

    private final LongestSubstringWithoutRepeating solution = new LongestSubstringWithoutRepeating();

    @Test
    void shouldFindLongest() {
        assertThat(solution.solve("abcabcbb")).isEqualTo(3);
    }

    @Test
    void shouldHandleAllSame() {
        assertThat(solution.solve("bbbbb")).isEqualTo(1);
    }

    @Test
    void shouldHandleMixed() {
        assertThat(solution.solve("pwwkew")).isEqualTo(3);
    }

    @Test
    void shouldHandleEmpty() {
        assertThat(solution.solve("")).isEqualTo(0);
    }
}
