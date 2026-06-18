package com.denjossal.study.dsa.graph;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UnionFindTest {

    @Test
    void shouldStartWithAllDisconnected() {
        var uf = new UnionFind(5);

        assertThat(uf.components()).isEqualTo(5);
        assertThat(uf.connected(0, 1)).isFalse();
        assertThat(uf.connected(2, 3)).isFalse();
    }

    @Test
    void shouldUnionAndConnect() {
        var uf = new UnionFind(5);
        uf.union(0, 1);
        uf.union(2, 3);

        assertThat(uf.connected(0, 1)).isTrue();
        assertThat(uf.connected(2, 3)).isTrue();
        assertThat(uf.connected(0, 2)).isFalse();
        assertThat(uf.components()).isEqualTo(3);
    }

    @Test
    void shouldMergeComponents() {
        var uf = new UnionFind(5);
        uf.union(0, 1);
        uf.union(2, 3);
        uf.union(1, 3);

        assertThat(uf.connected(0, 3)).isTrue();
        assertThat(uf.connected(1, 2)).isTrue();
        assertThat(uf.components()).isEqualTo(2);
    }

    @Test
    void shouldReturnFalseOnRedundantUnion() {
        var uf = new UnionFind(3);
        assertThat(uf.union(0, 1)).isTrue();
        assertThat(uf.union(0, 1)).isFalse();
    }

    @Test
    void shouldHandleSingleComponent() {
        var uf = new UnionFind(4);
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(2, 3);

        assertThat(uf.components()).isEqualTo(1);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertThat(uf.connected(i, j)).isTrue();
            }
        }
    }

    @Test
    void shouldFindReturnSameRootAfterPathCompression() {
        var uf = new UnionFind(6);
        // Build a chain: 0-1-2-3-4-5
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(2, 3);
        uf.union(3, 4);
        uf.union(4, 5);

        int root = uf.find(5);
        // After path compression, all should point to same root
        assertThat(uf.find(0)).isEqualTo(root);
        assertThat(uf.find(3)).isEqualTo(root);
        assertThat(uf.find(5)).isEqualTo(root);
    }

    @Test
    void shouldDetectCycleInUndirectedEdgeList() {
        // Classic use case: given edges, detect if adding one creates a cycle
        int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 0}};
        var uf = new UnionFind(4);

        boolean hasCycle = false;
        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) {
                hasCycle = true;
                break;
            }
        }

        assertThat(hasCycle).isTrue();
    }

    @Test
    void shouldNotDetectCycleInTree() {
        int[][] edges = {{0, 1}, {1, 2}, {2, 3}};
        var uf = new UnionFind(4);

        boolean hasCycle = false;
        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) {
                hasCycle = true;
                break;
            }
        }

        assertThat(hasCycle).isFalse();
    }
}
