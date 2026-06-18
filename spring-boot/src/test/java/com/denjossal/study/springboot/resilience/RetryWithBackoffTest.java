package com.denjossal.study.springboot.resilience;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class RetryWithBackoffTest {

    @Test
    void shouldSucceedOnFirstAttempt() {
        var retry = new RetryWithBackoff(3, 10, 2.0);
        var result = retry.execute(() -> "ok");
        assertThat(result).isEqualTo("ok");
    }

    @Test
    void shouldRetryAndSucceed() {
        var retry = new RetryWithBackoff(3, 10, 2.0);
        var attempts = new AtomicInteger(0);

        var result = retry.execute(() -> {
            if (attempts.incrementAndGet() < 3) {
                throw new RuntimeException("transient failure");
            }
            return "success on attempt 3";
        });

        assertThat(result).isEqualTo("success on attempt 3");
        assertThat(attempts.get()).isEqualTo(3);
    }

    @Test
    void shouldThrowAfterMaxAttempts() {
        var retry = new RetryWithBackoff(3, 10, 2.0);

        assertThatThrownBy(() -> retry.execute(() -> {
                    throw new RuntimeException("always fails");
                }))
                .isInstanceOf(RetryWithBackoff.RetryExhaustedException.class)
                .hasMessageContaining("3 attempts");
    }
}
