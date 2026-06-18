package com.denjossal.study.springboot.messaging;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class IdempotentConsumerTest {

    @Test
    void shouldProcessUniqueEvents() {
        var processed = new ArrayList<String>();
        var consumer = new IdempotentConsumer<String>(e -> processed.add(e.payload()));

        consumer.consume(Event.of("order.created", "src", "order-1"));
        consumer.consume(Event.of("order.created", "src", "order-2"));

        assertThat(processed).containsExactly("order-1", "order-2");
        assertThat(consumer.getProcessedCount()).isEqualTo(2);
    }

    @Test
    void shouldSkipDuplicates() {
        var processed = new ArrayList<String>();
        var consumer = new IdempotentConsumer<String>(e -> processed.add(e.payload()));

        var event = Event.of("order.created", "src", "order-1");
        consumer.consume(event);
        consumer.consume(event); // same event again

        assertThat(processed).hasSize(1);
        assertThat(consumer.getDuplicateCount()).isEqualTo(1);
    }

    @Test
    void shouldTrackProcessedStatus() {
        var consumer = new IdempotentConsumer<String>(e -> {});
        var event = Event.of("test", "src", "data");

        assertThat(consumer.isProcessed(event.eventId())).isFalse();
        consumer.consume(event);
        assertThat(consumer.isProcessed(event.eventId())).isTrue();
    }
}
