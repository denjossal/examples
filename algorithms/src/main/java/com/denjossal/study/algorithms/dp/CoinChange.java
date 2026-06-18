package com.denjossal.study.algorithms.dp;

import java.util.Arrays;

/**
 * LeetCode 322 — Coin Change (Medium)
 * Pattern: 1-D DP (unbounded knapsack variant)
 * Time: O(amount * coins.length)
 * Space: O(amount)
 */
public class CoinChange {

    public int solve(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }
}
