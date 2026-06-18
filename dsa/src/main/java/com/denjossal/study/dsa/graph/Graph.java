package com.denjossal.study.dsa.graph;

import java.util.*;

/**
 * Unweighted graph using adjacency list representation.
 * Supports directed and undirected edges, BFS, DFS, cycle detection, topological sort.
 *
 * Complexities:
 *   addVertex:   O(1)
 *   addEdge:     O(1)
 *   BFS/DFS:     O(V + E)
 *   hasCycle:    O(V + E)
 *   topSort:     O(V + E)
 */
public class Graph<T> {

    private final Map<T, List<T>> adjacencyList;
    private final boolean directed;

    public Graph(boolean directed) {
        this.adjacencyList = new LinkedHashMap<>();
        this.directed = directed;
    }

    public void addVertex(T vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(T from, T to) {
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        if (!directed) {
            adjacencyList.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
        } else {
            adjacencyList.putIfAbsent(to, new ArrayList<>());
        }
    }

    public List<T> getNeighbors(T vertex) {
        return adjacencyList.getOrDefault(vertex, Collections.emptyList());
    }

    public Set<T> getVertices() {
        return adjacencyList.keySet();
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    /**
     * Breadth-first search from a starting vertex.
     * Returns vertices in BFS visit order.
     */
    public List<T> bfs(T start) {
        List<T> result = new ArrayList<>();
        if (!adjacencyList.containsKey(start)) return result;

        Set<T> visited = new HashSet<>();
        Queue<T> queue = new LinkedList<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            T current = queue.poll();
            result.add(current);

            for (T neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return result;
    }

    /**
     * Depth-first search from a starting vertex.
     * Returns vertices in DFS visit order.
     */
    public List<T> dfs(T start) {
        List<T> result = new ArrayList<>();
        if (!adjacencyList.containsKey(start)) return result;

        Set<T> visited = new HashSet<>();
        dfsRec(start, visited, result);
        return result;
    }

    /**
     * Shortest path (unweighted) from source to target using BFS.
     * Returns the path as a list, or empty if no path exists.
     */
    public List<T> shortestPath(T source, T target) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(target)) {
            return Collections.emptyList();
        }

        Map<T, T> parent = new LinkedHashMap<>();
        Set<T> visited = new HashSet<>();
        Queue<T> queue = new LinkedList<>();

        visited.add(source);
        queue.add(source);
        parent.put(source, null);

        while (!queue.isEmpty()) {
            T current = queue.poll();
            if (current.equals(target)) {
                return reconstructPath(parent, target);
            }
            for (T neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Detects cycle in the graph.
     * For directed graphs: uses coloring (white/gray/black).
     * For undirected graphs: uses parent tracking.
     */
    public boolean hasCycle() {
        if (directed) return hasCycleDirected();
        return hasCycleUndirected();
    }

    /**
     * Topological sort (directed graphs only).
     * Returns empty if the graph has a cycle.
     */
    public List<T> topologicalSort() {
        if (!directed) throw new UnsupportedOperationException("Topological sort requires a directed graph");
        if (hasCycle()) return Collections.emptyList();

        Set<T> visited = new HashSet<>();
        Deque<T> stack = new ArrayDeque<>();

        for (T vertex : adjacencyList.keySet()) {
            if (!visited.contains(vertex)) {
                topSortDfs(vertex, visited, stack);
            }
        }

        return new ArrayList<>(stack);
    }

    /**
     * Counts connected components (undirected graphs).
     */
    public int connectedComponents() {
        if (directed) throw new UnsupportedOperationException("Use for undirected graphs");

        Set<T> visited = new HashSet<>();
        int count = 0;

        for (T vertex : adjacencyList.keySet()) {
            if (!visited.contains(vertex)) {
                bfsVisit(vertex, visited);
                count++;
            }
        }
        return count;
    }

    private void dfsRec(T vertex, Set<T> visited, List<T> result) {
        visited.add(vertex);
        result.add(vertex);
        for (T neighbor : getNeighbors(vertex)) {
            if (!visited.contains(neighbor)) {
                dfsRec(neighbor, visited, result);
            }
        }
    }

    private List<T> reconstructPath(Map<T, T> parent, T target) {
        List<T> path = new ArrayList<>();
        T current = target;
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    private boolean hasCycleDirected() {
        Set<T> white = new HashSet<>(adjacencyList.keySet());
        Set<T> gray = new HashSet<>();
        Set<T> black = new HashSet<>();

        for (T vertex : adjacencyList.keySet()) {
            if (white.contains(vertex)) {
                if (dfsCycleDirected(vertex, white, gray, black)) return true;
            }
        }
        return false;
    }

    private boolean dfsCycleDirected(T vertex, Set<T> white, Set<T> gray, Set<T> black) {
        white.remove(vertex);
        gray.add(vertex);

        for (T neighbor : getNeighbors(vertex)) {
            if (gray.contains(neighbor)) return true;
            if (white.contains(neighbor) && dfsCycleDirected(neighbor, white, gray, black)) return true;
        }

        gray.remove(vertex);
        black.add(vertex);
        return false;
    }

    private boolean hasCycleUndirected() {
        Set<T> visited = new HashSet<>();
        for (T vertex : adjacencyList.keySet()) {
            if (!visited.contains(vertex)) {
                if (dfsCycleUndirected(vertex, null, visited)) return true;
            }
        }
        return false;
    }

    private boolean dfsCycleUndirected(T vertex, T parent, Set<T> visited) {
        visited.add(vertex);
        for (T neighbor : getNeighbors(vertex)) {
            if (!visited.contains(neighbor)) {
                if (dfsCycleUndirected(neighbor, vertex, visited)) return true;
            } else if (!neighbor.equals(parent)) {
                return true;
            }
        }
        return false;
    }

    private void topSortDfs(T vertex, Set<T> visited, Deque<T> stack) {
        visited.add(vertex);
        for (T neighbor : getNeighbors(vertex)) {
            if (!visited.contains(neighbor)) {
                topSortDfs(neighbor, visited, stack);
            }
        }
        stack.push(vertex);
    }

    private void bfsVisit(T start, Set<T> visited) {
        Queue<T> queue = new LinkedList<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            T current = queue.poll();
            for (T neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }
}
