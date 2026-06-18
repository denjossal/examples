package com.denjossal.study.algorithms.slidingwindow;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 3 — Longest Substring Without Repeating Characters (Medium)
 * Pattern: Sliding Window (expand right, shrink left on duplicate)
 * Time: O(n)
 * Space: O(min(n, charset))
 */
public class LongestSubstringWithoutRepeating {

    public int solve(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int maxLen = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
                left = lastSeen.get(c) + 1;
            }
            lastSeen.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}
