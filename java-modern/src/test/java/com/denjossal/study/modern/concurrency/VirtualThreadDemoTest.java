package com.denjossal.study.modern.concurrency;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VirtualThreadDemoTest {

    @Test
    void concurrentShouldBeFasterThanSequential() throws Exception {
        long[] times = VirtualThreadDemo.compareConcurrencyApproaches(100);
        long sequentialTime = times[0];
        long concurrentTime = times[1];

        assertThat(concurrentTime).isLessThan(sequentialTime);
    }

    @Test
    void shouldComposeWithCompletableFuture() throws Exception {
        String result = VirtualThreadDemo.fetchUserProfile(5).get();
        assertThat(result).contains("Result-5");
        assertThat(result).contains("age 30");
    }
}
