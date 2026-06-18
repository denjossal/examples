package com.denjossal.study.springboot.messaging;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Idempotent Consumer pattern — ensures messages are processed exactly once.
 *
 * Problem: at-least-once delivery means duplicates WILL arrive.
 * Solution: track processed message IDs and skip duplicates.
 *
 * In production: use a distributed store (Redis, DB) for the processed set.
 */
public class IdempotentConsumer<T> {

    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();
    private final Consumer<Event<T>> handler;
    private int processedCount = 0;
    private int duplicateCount = 0;

    public IdempotentConsumer(Consumer<Event<T>> handler) {
        this.handler = handler;
    }

    @SuppressWarnings("unchecked")
    public boolean consume(Event<?> event) {
        if (processedIds.contains(event.eventId())) {
            duplicateCount++;
            return false;
        }

        processedIds.add(event.eventId());
        handler.accept((Event<T>) event);
        processedCount++;
        return true;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }

    public boolean isProcessed(String eventId) {
        return processedIds.contains(eventId);
    }
}
