package com.denjossal.study.springboot.resilience;

import java.util.function.Supplier;

/**
 * Retry pattern with exponential backoff — handles transient failures.
 *
 * Strategy: retry up to maxAttempts with exponential delay between attempts.
 * Jitter can be added to prevent thundering herd.
 *
 * In production: use Spring Retry or Resilience4j.
 * This implementation demonstrates the core algorithm.
 */
public class RetryWithBackoff {

    private final int maxAttempts;
    private final long initialDelayMs;
    private final double multiplier;

    public RetryWithBackoff(int maxAttempts, long initialDelayMs, double multiplier) {
        this.maxAttempts = maxAttempts;
        this.initialDelayMs = initialDelayMs;
        this.multiplier = multiplier;
    }

    public <T> T execute(Supplier<T> action) {
        Exception lastException = null;
        long delay = initialDelayMs;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.get();
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxAttempts) {
                    sleep(delay);
                    delay = (long) (delay * multiplier);
                }
            }
        }
        throw new RetryExhaustedException("Failed after %d attempts".formatted(maxAttempts), lastException);
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", e);
        }
    }

    public static class RetryExhaustedException extends RuntimeException {
        public RetryExhaustedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
