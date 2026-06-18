package com.denjossal.study.springboot.performance;

import com.denjossal.study.springboot.performance.PerformanceTuning.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

class PerformanceTuningTest {

    @Test
    void shouldProcessInChunks() {
        var data = IntStream.range(0, 1000)
                .mapToObj(i -> "item-" + i)
                .toList();

        long total = PerformanceTuning.processInChunks(data, 100);

        assertThat(total).isGreaterThan(0);
    }

    @Test
    void shouldCalculateCpuBoundPoolSize() {
        int size = PerformanceTuning.cpuBoundPoolSize();
        assertThat(size).isGreaterThan(1);
        assertThat(size).isEqualTo(Runtime.getRuntime().availableProcessors() + 1);
    }

    @Test
    void shouldCalculateIoBoundPoolSize() {
        int size = PerformanceTuning.ioBoundPoolSize(1.0, 9.0);
        // For I/O-bound with wait/compute ratio of 9: cores * 1 * (1+9) = cores * 10
        assertThat(size).isEqualTo(Runtime.getRuntime().availableProcessors() * 10);
    }

    @Test
    void shouldProduceSameResultSequentialAndParallel() {
        var numbers = IntStream.rangeClosed(1, 1000).boxed().toList();

        long sequential = PerformanceTuning.sumSquaresSequential(numbers);
        long parallel = PerformanceTuning.sumSquaresParallel(numbers);

        assertThat(sequential).isEqualTo(parallel);
    }

    @Test
    void shouldCacheResults() {
        var cache = new ComputeCache<Integer, Integer>(3);

        cache.getOrCompute(1, k -> k * 10);
        cache.getOrCompute(2, k -> k * 10);
        cache.getOrCompute(1, k -> k * 10); // cache hit

        assertThat(cache.hitRate()).isCloseTo(0.333, within(0.01));
    }

    @Test
    void shouldEvictLRUFromCache() {
        var cache = new ComputeCache<Integer, Integer>(2);

        cache.getOrCompute(1, k -> 10);
        cache.getOrCompute(2, k -> 20);
        cache.getOrCompute(3, k -> 30); // evicts 1

        assertThat(cache.size()).isEqualTo(2);
        // 1 was evicted, accessing it again is a miss
        cache.getOrCompute(1, k -> 10);
        assertThat(cache.size()).isEqualTo(2);
    }

    @Test
    void shouldJoinStringsDifferentWays() {
        var items = List.of("a", "b", "c");

        String good = PerformanceTuning.joinGood(items);
        String best = PerformanceTuning.joinBest(items);

        assertThat(good).isEqualTo("a,b,c");
        assertThat(best).isEqualTo("a,b,c");
    }
}
