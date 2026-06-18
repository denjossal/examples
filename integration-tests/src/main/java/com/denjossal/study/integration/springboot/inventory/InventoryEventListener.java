package com.denjossal.study.integration.springboot.inventory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Kafka consumer that listens to order events and processes inventory.
 * Demonstrates Spring Kafka @KafkaListener integration.
 */
@Component
public class InventoryEventListener {

    private final List<String> processedEvents = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "order-events", groupId = "inventory-service")
    public void handleOrderEvent(String event) {
        processedEvents.add(event);
    }

    public List<String> getProcessedEvents() {
        return new ArrayList<>(processedEvents);
    }

    public void clear() {
        processedEvents.clear();
    }
}
