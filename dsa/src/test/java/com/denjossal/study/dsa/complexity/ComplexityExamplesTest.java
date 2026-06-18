package com.denjossal.study.dsa.complexity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ComplexityExamplesTest {

    @Test
    void shouldGetMiddleElement_O1() {
        assertThat(ComplexityExamples.getMiddleElement(new int[]{1, 2, 3, 4, 5})).isEqualTo(3);
    }

    @Test
    void shouldBinarySearch_OLogN() {
        int[] sorted = {1, 3, 5, 7, 9, 11, 13};
        assertThat(ComplexityExamples.binarySearch(sorted, 7)).isEqualTo(3);
        assertThat(ComplexityExamples.binarySearch(sorted, 4)).isEqualTo(-1);
    }

    @Test
    void shouldFindMax_ON() {
        assertThat(ComplexityExamples.findMax(new int[]{3, 1, 7, 2, 9, 4})).isEqualTo(9);
    }

    @Test
    void shouldDetectDuplicate_ON() {
        assertThat(ComplexityExamples.hasDuplicate(new int[]{1, 2, 3, 4, 5})).isFalse();
        assertThat(ComplexityExamples.hasDuplicate(new int[]{1, 2, 3, 2, 5})).isTrue();
    }

    @Test
    void shouldMergeSort_ONLogN() {
        int[] result = ComplexityExamples.mergeSort(new int[]{5, 2, 8, 1, 9, 3});
        assertThat(result).containsExactly(1, 2, 3, 5, 8, 9);
    }

    @Test
    void shouldMergeSortEmpty() {
        assertThat(ComplexityExamples.mergeSort(new int[]{})).isEmpty();
    }

    @Test
    void shouldFindPairWithSum_ON2() {
        assertThat(ComplexityExamples.hasPairWithSum(new int[]{1, 3, 5, 7}, 8)).isTrue();
        assertThat(ComplexityExamples.hasPairWithSum(new int[]{1, 3, 5, 7}, 20)).isFalse();
    }

    @Test
    void shouldComputeFibonacciNaive_O2N() {
        assertThat(ComplexityExamples.fibonacciNaive(10)).isEqualTo(55);
    }

    @Test
    void shouldComputeFibonacciMemoized_ON() {
        assertThat(ComplexityExamples.fibonacciMemoized(10)).isEqualTo(55);
        assertThat(ComplexityExamples.fibonacciMemoized(30)).isEqualTo(832040);
    }

    @Test
    void shouldBuildList_AmortizedO1() {
        var list = ComplexityExamples.buildList(1000);
        assertThat(list).hasSize(1000);
    }

    @Test
    void shouldSumIterative_O1Space() {
        assertThat(ComplexityExamples.sumIterative(new int[]{1, 2, 3, 4, 5})).isEqualTo(15);
    }

    @Test
    void shouldSumRecursive_ONSpace() {
        assertThat(ComplexityExamples.sumRecursive(new int[]{1, 2, 3, 4, 5}, 0)).isEqualTo(15);
    }
}
