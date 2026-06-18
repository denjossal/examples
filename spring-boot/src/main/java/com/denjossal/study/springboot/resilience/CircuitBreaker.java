package com.denjossal.study.springboot.resilience;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Circuit Breaker pattern — prevents cascading failures in distributed systems.
 *
 * States:
 *   CLOSED  → requests pass through; failures counted
 *   OPEN    → requests short-circuited; fallback returned
 *   HALF_OPEN → limited requests allowed to test recovery
 *
 * Transitions:
 *   CLOSED → OPEN: failure count exceeds threshold
 *   OPEN → HALF_OPEN: timeout expires
 *   HALF_OPEN → CLOSED: successful probe
 *   HALF_OPEN → OPEN: probe fails
 */
public class CircuitBreaker<T> {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreaker.class);

    public enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    private final int failureThreshold;
    private final long openDurationMs;
    private final Supplier<T> fallback;

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private volatile Instant openedAt;

    public CircuitBreaker(int failureThreshold, long openDurationMs, Supplier<T> fallback) {
        this.failureThreshold = failureThreshold;
        this.openDurationMs = openDurationMs;
        this.fallback = fallback;
    }

    public T execute(Supplier<T> action) {
        if (state.get() == State.OPEN) {
            if (isTimeoutExpired()) {
                state.set(State.HALF_OPEN);
                log.info("Circuit transition OPEN -> HALF_OPEN: open duration elapsed, probing");
            } else {
                log.debug("Circuit OPEN: short-circuiting call, returning fallback");
                return fallback.get();
            }
        }

        try {
            T result = action.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            log.warn("Circuit call failed ({} consecutive): {}", failureCount.get(), e.toString());
            return fallback.get();
        }
    }

    public State getState() {
        return state.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    private void onSuccess() {
        State previous = state.getAndSet(State.CLOSED);
        failureCount.set(0);
        if (previous != State.CLOSED) {
            log.info("Circuit transition {} -> CLOSED: probe succeeded", previous);
        }
    }

    private void onFailure() {
        int failures = failureCount.incrementAndGet();
        if (failures >= failureThreshold && state.get() != State.OPEN) {
            state.set(State.OPEN);
            openedAt = Instant.now();
            log.warn("Circuit transition -> OPEN: {} failures reached threshold {}", failures, failureThreshold);
        }
    }

    private boolean isTimeoutExpired() {
        return openedAt != null && Instant.now().toEpochMilli() - openedAt.toEpochMilli() >= openDurationMs;
    }
}
