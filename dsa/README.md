# DSA — Data Structures From Scratch + Big O

Core data structures reimplemented from first principles, with annotated Big O analysis. JDK 25, JUnit 5 + AssertJ.

Package root: `com.denjossal.study.dsa`. Every structure ships with a matching test under `src/test`.

## Big O (`complexity`)

`ComplexityExamples` — annotated reference for each complexity class (O(1) → O(2^n)) plus amortized and space cost. Includes binary search, merge sort, naive vs. memoized Fibonacci, and the "constraint → required complexity" cheat sheet.

## List (`list`)

| Class | Key operations | Complexity |
|-------|----------------|------------|
| `DynamicArray` | `add` / `get` / `set` / `remove(i)` | amortized O(1) append, O(1) access, O(n) remove |
| `SinglyLinkedList` | `addFirst` / `removeFirst` | O(1); `addLast`/`get`/`reverse` O(n); `findMiddle`/`hasCycle` via fast-slow pointers |
| `DoublyLinkedList` | `addFirst`/`addLast`/`removeFirst`/`removeLast` | O(1) (sentinel node); `get(i)`/`remove(i)` O(n) |

## Stack (`stack`) & Queue (`queue`)

| Class | Key operations | Complexity |
|-------|----------------|------------|
| `ArrayStack` | `push` / `pop` / `peek` | amortized O(1) push, O(1) pop/peek |
| `CircularArrayQueue` | `enqueue` / `dequeue` / `peek` | amortized O(1) enqueue, O(1) dequeue (head/tail wraparound) |

## HashTable (`hashtable`)

| Class | Key operations | Complexity |
|-------|----------------|------------|
| `HashMap` | `put` / `get` / `remove` / `containsKey` | O(1) avg (O(n) worst). Separate chaining, 0.75 load factor, ×2 resize |

## Heap (`heap`)

| Class | Key operations | Complexity |
|-------|----------------|------------|
| `MinHeap` | `offer` / `poll` | O(log n); `peek` O(1); build via `heapify` O(n). Array-backed, custom comparator |

## Tree (`tree`)

| Class | Key operations | Complexity |
|-------|----------------|------------|
| `BinarySearchTree` | `insert` / `contains` / `delete` | O(log n) balanced, O(n) degenerate; in/pre/post/level-order traversals |
| `Trie` | `insert` / `search` / `startsWith` / `delete` | O(m) per op (m = word length); `countWordsWithPrefix` |

## Graph (`graph`)

| Class | Key operations | Complexity |
|-------|----------------|------------|
| `Graph<T>` | `bfs` / `dfs` / `shortestPath` / `hasCycle` / `topologicalSort` / `connectedComponents` | O(V + E); adjacency list, directed & undirected |
| `UnionFind` | `find` / `union` / `connected` | ~O(α(n)) amortized; path compression + union by rank |

## Running

```bash
mvn test -pl dsa
```
