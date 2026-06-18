package com.denjossal.study.springboot.performance;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Performance tuning patterns for JVM applications.
 *
 * Key areas:
 * - JVM/GC: G1 vs ZGC, heap sizing, GC pause targets
 * - Concurrency: virtual threads for I/O, thread pools for CPU
 * - Streaming: process large data without full-file buffering
 * - Caching: avoid redundant computation
 * - Profiling: JFR (Java Flight Recorder), async-profiler
 *
 * Rules of thumb:
 * 1. Measure first (don't guess the bottleneck)
 * 2. I/O-bound → more threads / virtual threads / async
 * 3. CPU-bound → parallel streams / proper thread pool sizing (cores + 1)
 * 4. Memory-bound → streaming, off-heap, reduce object allocation
 */
public class PerformanceTuning {

    // ─── Streaming Large Data (no full-file buffering) ──────────────────────

    /**
     * Process data in chunks — simulates streaming a large file.
     * Key: never load the entire dataset into memory.
     */
    public static long processInChunks(List<String> largeDataset, int chunkSize) {
        long processed = 0;
        for (int i = 0; i < largeDataset.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, largeDataset.size());
            var chunk = largeDataset.subList(i, end);
            processed += processChunk(chunk);
        }
        return processed;
    }

    private static long processChunk(List<String> chunk) {
        return chunk.stream().mapToLong(String::length).sum();
    }

    // ─── Thread Pool Sizing ─────────────────────────────────────────────────

    /**
     * Optimal thread pool size depends on the workload type.
     */
    public static int cpuBoundPoolSize() {
        return Runtime.getRuntime().availableProcessors() + 1;
    }

    public static int ioBoundPoolSize(double targetUtilization, double waitToComputeRatio) {
        int cores = Runtime.getRuntime().availableProcessors();
        return (int) (cores * targetUtilization * (1 + waitToComputeRatio));
    }

    // ─── Parallel vs Sequential Decision ────────────────────────────────────

    /**
     * Demonstrates when parallel streams help vs hurt.
     * Rule: parallelize only when data is large AND operation is CPU-intensive.
     */
    public static long sumSquaresSequential(List<Integer> numbers) {
        return numbers.stream().mapToLong(n -> (long) n * n).sum();
    }

    public static long sumSquaresParallel(List<Integer> numbers) {
        return numbers.parallelStream().mapToLong(n -> (long) n * n).sum();
    }

    // ─── Caching Pattern ────────────────────────────────────────────────────

    /**
     * Simple LRU cache to avoid redundant expensive computations.
     */
    public static class ComputeCache<K, V> {
        private final Map<K, V> cache;
        private int hits = 0;
        private int misses = 0;

        public ComputeCache(int maxSize) {
            this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > maxSize;
                }
            };
        }

        public V getOrCompute(K key, java.util.function.Function<K, V> compute) {
            if (cache.containsKey(key)) {
                hits++;
                return cache.get(key);
            }
            misses++;
            V value = compute.apply(key);
            cache.put(key, value);
            return value;
        }

        public double hitRate() {
            int total = hits + misses;
            return total == 0 ? 0 : (double) hits / total;
        }

        public int size() {
            return cache.size();
        }
    }

    // ─── Object Allocation Reduction ────────────────────────────────────────

    /**
     * Bad: creates a new StringBuilder per iteration (allocation pressure).
     */
    public static String joinBad(List<String> items) {
        String result = "";
        for (var item : items) {
            result = result + item + ","; // creates new String each time
        }
        return result;
    }

    /**
     * Good: single StringBuilder, pre-sized.
     */
    public static String joinGood(List<String> items) {
        var sb = new StringBuilder(items.size() * 10);
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(items.get(i));
        }
        return sb.toString();
    }

    /**
     * Best: use built-in String.join (optimized internally).
     */
    public static String joinBest(List<String> items) {
        return String.join(",", items);
    }
}
