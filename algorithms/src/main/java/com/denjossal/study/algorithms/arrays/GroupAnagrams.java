package com.denjossal.study.algorithms.arrays;

import java.util.*;

/**
 * LeetCode 49 — Group Anagrams (Medium)
 * Pattern: Arrays & Hashing
 * Time: O(n * k log k) where n = number of strings, k = max string length
 * Space: O(n * k)
 */
public class GroupAnagrams {

    public List<List<String>> solve(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(groups.values());
    }
}
