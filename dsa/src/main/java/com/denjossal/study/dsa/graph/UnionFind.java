package com.denjossal.study.dsa.graph;

/**
 * Union-Find (Disjoint Set Union) with path compression and union by rank.
 * Used for: connected components, cycle detection in undirected graphs, Kruskal's MST.
 *
 * Complexities (with both optimizations):
 *   find:    O(α(n)) ≈ O(1) amortized (inverse Ackermann)
 *   union:   O(α(n)) ≈ O(1) amortized
 *   connected: O(α(n))
 */
public class UnionFind {

    private final int[] parent;
    private final int[] rank;
    private int components;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    /**
     * Finds the root/representative of the set containing x.
     * Uses path compression to flatten the tree.
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /**
     * Merges the sets containing x and y.
     * Uses union by rank to keep trees balanced.
     * Returns true if a merge occurred (they were in different sets).
     */
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) return false;

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        components--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int components() {
        return components;
    }

    public int size() {
        return parent.length;
    }
}
