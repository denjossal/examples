package com.denjossal.study.springboot.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OutboxPatternTest {

    @Test
    void shouldSaveToOutbox() {
        var outbox = new OutboxPattern();
        outbox.saveWithOutbox("order-1", "order.created", "{\"id\":\"order-1\"}");

        assertThat(outbox.getPendingEntries()).hasSize(1);
        assertThat(outbox.getPublishedEvents()).isEmpty();
    }

    @Test
    void shouldPollAndPublish() {
        var outbox = new OutboxPattern();
        outbox.saveWithOutbox("order-1", "order.created", "payload-1");
        outbox.saveWithOutbox("order-2", "order.created", "payload-2");

        int published = outbox.pollAndPublish();

        assertThat(published).isEqualTo(2);
        assertThat(outbox.getPublishedEvents()).containsExactly("payload-1", "payload-2");
        assertThat(outbox.getPendingEntries()).isEmpty();
    }

    @Test
    void shouldNotRepublishAlreadyPublished() {
        var outbox = new OutboxPattern();
        outbox.saveWithOutbox("order-1", "order.created", "payload-1");

        outbox.pollAndPublish();
        int secondPoll = outbox.pollAndPublish();

        assertThat(secondPoll).isEqualTo(0);
        assertThat(outbox.getPublishedEvents()).hasSize(1);
    }
}
