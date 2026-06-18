package com.denjossal.study.springboot.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * Base event record for event-driven architecture.
 * Every event has a unique ID, timestamp, type, and source.
 */
public record Event<T>(
        String eventId,
        String type,
        String source,
        Instant timestamp,
        T payload
) {
    public static <T> Event<T> of(String type, String source, T payload) {
        return new Event<>(UUID.randomUUID().toString(), type, source, Instant.now(), payload);
    }
}
