package com.denjossal.study.springboot.messaging;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Simple in-memory event bus demonstrating pub/sub patterns.
 *
 * In production: use Spring ApplicationEventPublisher, Kafka, SQS/SNS, or EventBridge.
 * This demonstrates the core pattern: decouple producers from consumers.
 */
public class InMemoryEventBus {

    private final Map<String, List<Consumer<Event<?>>>> subscribers = new ConcurrentHashMap<>();
    private final List<Event<?>> deadLetterQueue = new CopyOnWriteArrayList<>();

    public void subscribe(String eventType, Consumer<Event<?>> handler) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    public void publish(Event<?> event) {
        var handlers = subscribers.get(event.type());
        if (handlers == null || handlers.isEmpty()) {
            deadLetterQueue.add(event);
            return;
        }

        for (var handler : handlers) {
            try {
                handler.accept(event);
            } catch (Exception e) {
                deadLetterQueue.add(event);
            }
        }
    }

    public List<Event<?>> getDeadLetterQueue() {
        return Collections.unmodifiableList(deadLetterQueue);
    }

    public int subscriberCount(String eventType) {
        var handlers = subscribers.get(eventType);
        return handlers != null ? handlers.size() : 0;
    }
}
