# Algorithms — LeetCode by Pattern

LeetCode solutions grouped by the pattern that solves them, each with an intuition note and complexity in the source javadoc. JDK 25, JUnit 5 + AssertJ.

Package root: `com.denjossal.study.algorithms`. One package per pattern, one test per problem.

## Arrays & Hashing (`arrays`)

| Problem | Intuition |
|---------|-----------|
| 49 Group Anagrams | Bucket strings by their sorted-char key |
| 238 Product of Array Except Self | Prefix products left, then suffix products right |
| 347 Top K Frequent | Count frequencies, then bucket sort / heap |

## Two Pointers (`twopointers`)

| Problem | Intuition |
|---------|-----------|
| 11 Container With Most Water | Move the shorter wall inward (greedy narrowing) |
| 15 3Sum | Sort, fix one element, two-pointer sweep the rest |

## Sliding Window (`slidingwindow`)

| Problem | Intuition |
|---------|-----------|
| 3 Longest Substring Without Repeating | Expand right, shrink left on duplicate |
| 424 Longest Repeating Char Replacement | Valid while window − maxFreq ≤ k |

## Binary Search (`binarysearch`)

| Problem | Intuition |
|---------|-----------|
| 33 Search in Rotated Sorted Array | Decide which half is sorted, then recurse there |
| 153 Find Minimum in Rotated Sorted Array | Converge on the rotation inflection point |

## Stack (`stack`)

| Problem | Intuition |
|---------|-----------|
| 739 Daily Temperatures | Monotonic decreasing stack of indices |

## Linked List (`linkedlist`)

| Problem | Intuition |
|---------|-----------|
| 143 Reorder List | Find middle, reverse second half, merge alternately |
| 146 LRU Cache | Hash map + doubly-linked list for O(1) get/put |

## Trees (`trees`)

| Problem | Intuition |
|---------|-----------|
| 98 Validate BST | Pass a valid (min, max) range down each branch |
| 102 Level Order Traversal | BFS with per-level grouping |
| 236 Lowest Common Ancestor | Post-order DFS, bubble up the non-null results |

## Heap / Top-K (`heap`)

| Problem | Intuition |
|---------|-----------|
| 703 Kth Largest in a Stream | Min-heap of size k; root is the answer |
| 973 K Closest Points to Origin | Max-heap of size k by distance |

## Backtracking (`backtracking`)

| Problem | Intuition |
|---------|-----------|
| 39 Combination Sum | Unbounded choices; prune when sum exceeds target |
| 46 Permutations | Swap / used-array to fix each position |
| 78 Subsets | Include/exclude decision tree |

## Graphs (`graphs`)

| Problem | Intuition |
|---------|-----------|
| 200 Number of Islands | Flood-fill each unvisited land cell (BFS/DFS) |
| 207 Course Schedule | Cycle detection / topological sort on a DAG |

## Greedy (`greedy`)

| Problem | Intuition |
|---------|-----------|
| 53 Maximum Subarray (Kadane) | Extend the running sum or restart at the current element |
| 55 Jump Game | Track the farthest reachable index |

## Intervals (`intervals`)

| Problem | Intuition |
|---------|-----------|
| 56 Merge Intervals | Sort by start, merge overlapping |
| 57 Insert Interval | Three-phase sweep: before, overlap, after |

## Dynamic Programming (`dp`)

| Problem | Intuition |
|---------|-----------|
| 198 House Robber | Rob or skip each house (1-D DP) |
| 300 Longest Increasing Subsequence | Patience sorting + binary search → O(n log n) |
| 322 Coin Change | Unbounded knapsack over the amount |

## Running

```bash
mvn test -pl algorithms
```
