package com.denjossal.study.dsa.graph;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GraphTest {

    @Test
    void shouldCreateUndirectedGraph() {
        var graph = new Graph<Integer>(false);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);

        assertThat(graph.vertexCount()).isEqualTo(4);
        assertThat(graph.getNeighbors(1)).containsExactlyInAnyOrder(2, 3);
        assertThat(graph.getNeighbors(2)).containsExactlyInAnyOrder(1, 4);
    }

    @Test
    void shouldCreateDirectedGraph() {
        var graph = new Graph<String>(true);
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");

        assertThat(graph.getNeighbors("A")).containsExactlyInAnyOrder("B", "C");
        assertThat(graph.getNeighbors("B")).isEmpty();
    }

    @Test
    void shouldBFS() {
        var graph = new Graph<Integer>(false);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 5);

        var result = graph.bfs(1);

        assertThat(result).startsWith(1);
        assertThat(result).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
        // Level 1 (2,3) should come before level 2 (4,5)
        assertThat(result.indexOf(2)).isLessThan(result.indexOf(4));
        assertThat(result.indexOf(3)).isLessThan(result.indexOf(5));
    }

    @Test
    void shouldDFS() {
        var graph = new Graph<Integer>(false);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);

        var result = graph.dfs(1);

        assertThat(result).startsWith(1);
        assertThat(result).containsExactlyInAnyOrder(1, 2, 3, 4);
    }

    @Test
    void shouldFindShortestPath() {
        var graph = new Graph<Integer>(false);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);

        var path = graph.shortestPath(1, 5);

        // Shortest path: 1 -> 2 -> 4 -> 5 or 1 -> 3 -> 4 -> 5 (both length 4 nodes)
        assertThat(path).hasSize(4);
        assertThat(path.get(0)).isEqualTo(1);
        assertThat(path.get(path.size() - 1)).isEqualTo(5);
    }

    @Test
    void shouldReturnEmptyPathWhenNoRoute() {
        var graph = new Graph<Integer>(false);
        graph.addVertex(1);
        graph.addVertex(2);

        assertThat(graph.shortestPath(1, 2)).isEmpty();
    }

    @Test
    void shouldDetectCycleInUndirectedGraph() {
        var graph = new Graph<Integer>(false);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);

        assertThat(graph.hasCycle()).isTrue();
    }

    @Test
    void shouldDetectNoCycleInUndirectedGraph() {
        var graph = new Graph<Integer>(false);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);

        assertThat(graph.hasCycle()).isFalse();
    }

    @Test
    void shouldDetectCycleInDirectedGraph() {
        var graph = new Graph<Integer>(true);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);

        assertThat(graph.hasCycle()).isTrue();
    }

    @Test
    void shouldDetectNoCycleInDAG() {
        var graph = new Graph<Integer>(true);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);

        assertThat(graph.hasCycle()).isFalse();
    }

    @Test
    void shouldTopologicalSort() {
        var graph = new Graph<String>(true);
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");

        var sorted = graph.topologicalSort();

        assertThat(sorted).hasSize(4);
        assertThat(sorted.indexOf("A")).isLessThan(sorted.indexOf("B"));
        assertThat(sorted.indexOf("A")).isLessThan(sorted.indexOf("C"));
        assertThat(sorted.indexOf("B")).isLessThan(sorted.indexOf("D"));
        assertThat(sorted.indexOf("C")).isLessThan(sorted.indexOf("D"));
    }

    @Test
    void shouldReturnEmptyTopSortOnCycle() {
        var graph = new Graph<Integer>(true);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);

        assertThat(graph.topologicalSort()).isEmpty();
    }

    @Test
    void shouldCountConnectedComponents() {
        var graph = new Graph<Integer>(false);
        // Component 1
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        // Component 2
        graph.addEdge(4, 5);
        // Component 3 (isolated)
        graph.addVertex(6);

        assertThat(graph.connectedComponents()).isEqualTo(3);
    }

    @Test
    void shouldReturnEmptyForNonExistentVertex() {
        var graph = new Graph<Integer>(false);
        graph.addEdge(1, 2);

        assertThat(graph.bfs(99)).isEmpty();
        assertThat(graph.dfs(99)).isEmpty();
    }
}
