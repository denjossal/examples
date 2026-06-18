package com.denjossal.study.dsa.complexity;

import java.util.*;

/**
 * Big O Complexity Analysis — annotated examples for each complexity class.
 *
 * Practical rule: given constraint n, infer required complexity:
 *   n ≤ 10       → O(n!) or O(2^n)  — brute force ok
 *   n ≤ 20       → O(2^n)           — bitmask/backtracking
 *   n ≤ 500      → O(n^3)           — triple nested loops
 *   n ≤ 5,000    → O(n^2)           — double nested loops
 *   n ≤ 10^5     → O(n log n)       — sort + scan
 *   n ≤ 10^6     → O(n)             — linear scan
 *   n ≤ 10^18    → O(log n) or O(1) — binary search / math
 */
public class ComplexityExamples {

    // ─── O(1) — Constant ────────────────────────────────────────────────────

    /**
     * O(1) time, O(1) space.
     * Array index access, hash map get, stack push/pop.
     */
    public static int getMiddleElement(int[] arr) {
        return arr[arr.length / 2];
    }

    // ─── O(log n) — Logarithmic ────────────────────────────────────────────

    /**
     * O(log n) time, O(1) space.
     * Halving the search space each iteration.
     */
    public static int binarySearch(int[] sorted, int target) {
        int lo = 0, hi = sorted.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (sorted[mid] == target) return mid;
            if (sorted[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    // ─── O(n) — Linear ─────────────────────────────────────────────────────

    /**
     * O(n) time, O(1) space.
     * Single pass through the data.
     */
    public static int findMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        return max;
    }

    /**
     * O(n) time, O(n) space — hash set for O(1) lookups.
     */
    public static boolean hasDuplicate(int[] arr) {
        var seen = new HashSet<Integer>();
        for (int val : arr) {
            if (!seen.add(val)) return true;
        }
        return false;
    }

    // ─── O(n log n) — Linearithmic ─────────────────────────────────────────

    /**
     * O(n log n) time, O(n) space (merge sort).
     * Divide-and-conquer: split → recurse → merge.
     */
    public static int[] mergeSort(int[] arr) {
        if (arr.length <= 1) return arr;

        int mid = arr.length / 2;
        int[] left = mergeSort(Arrays.copyOfRange(arr, 0, mid));
        int[] right = mergeSort(Arrays.copyOfRange(arr, mid, arr.length));

        return merge(left, right);
    }

    private static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            result[k++] = left[i] <= right[j] ? left[i++] : right[j++];
        }
        while (i < left.length) result[k++] = left[i++];
        while (j < right.length) result[k++] = right[j++];
        return result;
    }

    // ─── O(n^2) — Quadratic ────────────────────────────────────────────────

    /**
     * O(n^2) time, O(1) space.
     * Nested loops — each element compared with every other.
     */
    public static boolean hasPairWithSum(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] + arr[j] == target) return true;
            }
        }
        return false;
    }

    // ─── O(2^n) — Exponential ───────────────────────────────────────────────

    /**
     * O(2^n) time, O(n) space (recursion stack).
     * Each call branches into two: classic exponential tree.
     */
    public static int fibonacciNaive(int n) {
        if (n <= 1) return n;
        return fibonacciNaive(n - 1) + fibonacciNaive(n - 2);
    }

    /**
     * O(n) time via memoization — reduces exponential to linear.
     * Demonstrates how DP eliminates overlapping subproblems.
     */
    public static int fibonacciMemoized(int n) {
        return fibMemo(n, new HashMap<>());
    }

    private static int fibMemo(int n, Map<Integer, Integer> memo) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        int result = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
        memo.put(n, result);
        return result;
    }

    // ─── Amortized O(1) ─────────────────────────────────────────────────────

    /**
     * Amortized O(1) per add — occasional O(n) resize is spread across n operations.
     * ArrayList.add() is the classic example.
     */
    public static List<Integer> buildList(int n) {
        var list = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            list.add(i); // amortized O(1)
        }
        return list;
    }

    // ─── Space Complexity Examples ──────────────────────────────────────────

    /**
     * O(1) space — iterative, constant extra memory.
     */
    public static int sumIterative(int[] arr) {
        int sum = 0;
        for (int val : arr) sum += val;
        return sum;
    }

    /**
     * O(n) space — recursion stack depth equals input size.
     */
    public static int sumRecursive(int[] arr, int index) {
        if (index >= arr.length) return 0;
        return arr[index] + sumRecursive(arr, index + 1);
    }
}
