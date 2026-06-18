package com.denjossal.study.springboot.resilience;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

class BulkheadTest {

    @Test
    void shouldAllowWithinLimit() {
        var bulkhead = new Bulkhead<String>(5, 1000);
        var result = bulkhead.execute(() -> "ok");
        assertThat(result).isEqualTo("ok");
    }

    @Test
    void shouldRejectWhenFull() throws Exception {
        var bulkhead = new Bulkhead<String>(1, 50);
        var latch = new CountDownLatch(1);

        // Fill the single permit with a blocking task
        var executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> bulkhead.execute(() -> {
            try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "done";
        }));

        Thread.sleep(20); // let the first task acquire

        assertThatThrownBy(() -> bulkhead.execute(() -> "rejected"))
                .isInstanceOf(Bulkhead.BulkheadRejectedException.class);

        latch.countDown();
        executor.shutdown();
    }

    @Test
    void shouldTrackAvailablePermits() {
        var bulkhead = new Bulkhead<String>(3, 1000);
        assertThat(bulkhead.availablePermits()).isEqualTo(3);

        bulkhead.execute(() -> {
            assertThat(bulkhead.availablePermits()).isEqualTo(2);
            return "inside";
        });

        assertThat(bulkhead.availablePermits()).isEqualTo(3);
    }
}
