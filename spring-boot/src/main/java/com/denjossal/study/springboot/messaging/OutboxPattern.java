package com.denjossal.study.springboot.messaging;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Transactional Outbox pattern — guarantees at-least-once event delivery.
 *
 * Problem: writing to DB + publishing event is not atomic.
 *   Option A: publish first, DB fails → event published but data not saved
 *   Option B: DB first, publish fails → data saved but event lost
 *
 * Solution: write event to an "outbox" table in the SAME transaction as the data.
 * A separate process (poller/CDC) reads the outbox and publishes to the broker.
 *
 * This class simulates the pattern in-memory.
 */
public class OutboxPattern {

    public record OutboxEntry(
            String id,
            String aggregateId,
            String eventType,
            String payload,
            Instant createdAt,
            boolean published
    ) {}

    private final List<OutboxEntry> outbox = new CopyOnWriteArrayList<>();
    private final List<String> publishedEvents = new CopyOnWriteArrayList<>();

    /**
     * Simulates: save entity + write outbox entry in same "transaction".
     */
    public OutboxEntry saveWithOutbox(String aggregateId, String eventType, String payload) {
        var entry = new OutboxEntry(
                UUID.randomUUID().toString(),
                aggregateId,
                eventType,
                payload,
                Instant.now(),
                false
        );
        outbox.add(entry);
        return entry;
    }

    /**
     * Simulates the outbox poller: reads unpublished entries and publishes them.
     * In production: runs on a schedule or via CDC (Debezium).
     */
    public int pollAndPublish() {
        int published = 0;
        for (int i = 0; i < outbox.size(); i++) {
            var entry = outbox.get(i);
            if (!entry.published()) {
                publishedEvents.add(entry.payload());
                outbox.set(i, new OutboxEntry(
                        entry.id(), entry.aggregateId(), entry.eventType(),
                        entry.payload(), entry.createdAt(), true
                ));
                published++;
            }
        }
        return published;
    }

    public List<OutboxEntry> getPendingEntries() {
        return outbox.stream().filter(e -> !e.published()).toList();
    }

    public List<String> getPublishedEvents() {
        return Collections.unmodifiableList(publishedEvents);
    }
}
