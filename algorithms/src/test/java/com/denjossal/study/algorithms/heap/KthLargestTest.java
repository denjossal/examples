package com.denjossal.study.algorithms.heap;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class KthLargestTest {

    @Test
    void shouldTrackKthLargest() {
        var kth = new KthLargest(3, new int[]{4, 5, 8, 2});
        assertThat(kth.add(3)).isEqualTo(4);
        assertThat(kth.add(5)).isEqualTo(5);
        assertThat(kth.add(10)).isEqualTo(5);
        assertThat(kth.add(9)).isEqualTo(8);
        assertThat(kth.add(4)).isEqualTo(8);
    }
}
