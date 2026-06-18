package com.denjossal.study.springboot.resilience;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CircuitBreakerTest {

    @Test
    void shouldStartClosed() {
        var cb = new CircuitBreaker<>(3, 1000, () -> "fallback");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void shouldPassThroughWhenClosed() {
        var cb = new CircuitBreaker<>(3, 1000, () -> "fallback");
        var result = cb.execute(() -> "success");
        assertThat(result).isEqualTo("success");
    }

    @Test
    void shouldOpenAfterThreshold() {
        var cb = new CircuitBreaker<String>(3, 1000, () -> "fallback");

        for (int i = 0; i < 3; i++) {
            cb.execute(() -> {
                throw new RuntimeException("fail");
            });
        }

        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void shouldReturnFallbackWhenOpen() {
        var cb = new CircuitBreaker<String>(1, 5000, () -> "fallback");
        cb.execute(() -> {
            throw new RuntimeException();
        });

        var result = cb.execute(() -> "should not reach");
        assertThat(result).isEqualTo("fallback");
    }

    @Test
    void shouldResetOnSuccess() {
        var cb = new CircuitBreaker<String>(3, 1000, () -> "fallback");
        cb.execute(() -> {
            throw new RuntimeException();
        });
        cb.execute(() -> {
            throw new RuntimeException();
        });

        cb.execute(() -> "success");

        assertThat(cb.getFailureCount()).isEqualTo(0);
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void shouldTransitionToHalfOpenAfterTimeout() throws InterruptedException {
        var cb = new CircuitBreaker<String>(1, 50, () -> "fallback");
        cb.execute(() -> {
            throw new RuntimeException();
        });
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        Thread.sleep(60);

        var result = cb.execute(() -> "recovered");
        assertThat(result).isEqualTo("recovered");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }
}
