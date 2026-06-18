package com.denjossal.study.springboot.resilience;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bulkhead pattern — limits concurrent access to a resource.
 *
 * Prevents one slow dependency from consuming all threads.
 * Like ship bulkheads: a breach in one compartment doesn't sink the ship.
 *
 * In production: use Resilience4j Bulkhead or thread pool isolation.
 */
public class Bulkhead<T> {

    private static final Logger log = LoggerFactory.getLogger(Bulkhead.class);

    private final Semaphore semaphore;
    private final long timeoutMs;

    public Bulkhead(int maxConcurrency, long timeoutMs) {
        this.semaphore = new Semaphore(maxConcurrency);
        this.timeoutMs = timeoutMs;
    }

    public T execute(Supplier<T> action) {
        boolean acquired;
        try {
            acquired = semaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BulkheadRejectedException("Interrupted waiting for bulkhead");
        }

        if (!acquired) {
            log.warn(
                    "Bulkhead full — request rejected after waiting {}ms (0 of {} permits free)",
                    timeoutMs,
                    semaphore.availablePermits() + semaphore.getQueueLength());
            throw new BulkheadRejectedException("Bulkhead full — request rejected");
        }

        try {
            return action.get();
        } finally {
            semaphore.release();
        }
    }

    public int availablePermits() {
        return semaphore.availablePermits();
    }

    public static class BulkheadRejectedException extends RuntimeException {
        public BulkheadRejectedException(String message) {
            super(message);
        }
    }
}
