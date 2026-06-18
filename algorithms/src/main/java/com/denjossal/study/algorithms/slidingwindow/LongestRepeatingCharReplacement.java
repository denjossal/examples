package com.denjossal.study.algorithms.slidingwindow;

/**
 * LeetCode 424 — Longest Repeating Character Replacement (Medium)
 * Pattern: Sliding Window (window size - max freq <= k)
 * Time: O(n)
 * Space: O(1) — 26-char frequency array
 */
public class LongestRepeatingCharReplacement {

    public int solve(String s, int k) {
        int[] freq = new int[26];
        int maxFreq = 0;
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            freq[s.charAt(right) - 'A']++;
            maxFreq = Math.max(maxFreq, freq[s.charAt(right) - 'A']);

            int windowSize = right - left + 1;
            if (windowSize - maxFreq > k) {
                freq[s.charAt(left) - 'A']--;
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}
