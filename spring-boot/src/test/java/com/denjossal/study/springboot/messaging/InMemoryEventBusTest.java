package com.denjossal.study.springboot.messaging;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

class InMemoryEventBusTest {

    @Test
    void shouldPublishToSubscribers() {
        var bus = new InMemoryEventBus();
        var received = new ArrayList<String>();

        bus.subscribe("order.created", event -> received.add(event.payload().toString()));

        bus.publish(Event.of("order.created", "order-service", "ORDER-123"));

        assertThat(received).containsExactly("ORDER-123");
    }

    @Test
    void shouldSendToDLQWhenNoSubscribers() {
        var bus = new InMemoryEventBus();
        var event = Event.of("unknown.type", "source", "payload");

        bus.publish(event);

        assertThat(bus.getDeadLetterQueue()).hasSize(1);
    }

    @Test
    void shouldSupportMultipleSubscribers() {
        var bus = new InMemoryEventBus();
        var list1 = new ArrayList<String>();
        var list2 = new ArrayList<String>();

        bus.subscribe("user.signup", e -> list1.add("handler1"));
        bus.subscribe("user.signup", e -> list2.add("handler2"));

        bus.publish(Event.of("user.signup", "auth", "user-1"));

        assertThat(list1).hasSize(1);
        assertThat(list2).hasSize(1);
    }

    @Test
    void shouldSendToLDQOnHandlerFailure() {
        var bus = new InMemoryEventBus();
        bus.subscribe("risky", e -> { throw new RuntimeException("boom"); });

        bus.publish(Event.of("risky", "src", "data"));

        assertThat(bus.getDeadLetterQueue()).hasSize(1);
    }
}
