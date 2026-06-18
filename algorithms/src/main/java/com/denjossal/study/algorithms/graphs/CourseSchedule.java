package com.denjossal.study.algorithms.graphs;

import java.util.*;

/**
 * LeetCode 207 — Course Schedule (Medium)
 * Pattern: Graphs (topological sort / cycle detection in DAG)
 * Time: O(V + E)
 * Space: O(V + E)
 */
public class CourseSchedule {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());

        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
            inDegree[pre[0]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.add(i);
        }

        int completed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            completed++;
            for (int next : adj.get(course)) {
                inDegree[next]--;
                if (inDegree[next] == 0) queue.add(next);
            }
        }
        return completed == numCourses;
    }
}
