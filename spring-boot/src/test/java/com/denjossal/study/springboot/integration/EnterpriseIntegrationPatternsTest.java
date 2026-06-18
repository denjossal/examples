package com.denjossal.study.springboot.integration;

import static org.assertj.core.api.Assertions.*;

import com.denjossal.study.springboot.integration.EnterpriseIntegrationPatterns.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class EnterpriseIntegrationPatternsTest {

    @Test
    void shouldRouteByContent() {
        var messages = List.of(
                Message.of("order-123", "type", "order"),
                Message.of("payment-456", "type", "payment"),
                Message.of("unknown-789", "type", "unknown"));

        var orders = new ArrayList<Message>();
        var payments = new ArrayList<Message>();
        var deadLetters = new ArrayList<Message>();

        EnterpriseIntegrationPatterns.contentBasedRouter(
                messages,
                msg -> msg.headers().get("type"),
                Map.of("order", orders::add, "payment", payments::add),
                deadLetters::add);

        assertThat(orders).hasSize(1);
        assertThat(payments).hasSize(1);
        assertThat(deadLetters).hasSize(1);
    }

    @Test
    void shouldSplitCompositeMessage() {
        var composite = Message.of("item1,item2,item3", "orderId", "ORD-1");
        var parts = EnterpriseIntegrationPatterns.splitter(composite, ",");

        assertThat(parts).hasSize(3);
        assertThat(parts.get(0).body()).isEqualTo("item1");
        assertThat(parts.get(0).headers()).containsEntry("orderId", "ORD-1");
    }

    @Test
    void shouldAggregateMessages() {
        var messages =
                List.of(Message.of("result-1", "source", "service-a"), Message.of("result-2", "source", "service-b"));

        var aggregated = EnterpriseIntegrationPatterns.aggregator(messages, " | ");

        assertThat(aggregated.body()).isEqualTo("result-1 | result-2");
        assertThat(aggregated.headers()).containsEntry("aggregatedCount", "2");
    }

    @Test
    void shouldWireTapWithoutAffectingFlow() {
        var tapped = new ArrayList<Message>();
        var original = Message.of("important data");

        var result = EnterpriseIntegrationPatterns.wireTap(original, tapped::add);

        assertThat(result).isSameAs(original);
        assertThat(tapped).hasSize(1);
        assertThat(tapped.get(0).body()).isEqualTo("important data");
    }

    @Test
    void shouldClaimCheckInAndOut() {
        var large = Message.of("very large payload...");
        var claim = EnterpriseIntegrationPatterns.claimCheckIn(large);

        assertThat(claim.body()).startsWith("CLAIM:");
        assertThat(claim.headers()).containsKey("claimId");

        var retrieved = EnterpriseIntegrationPatterns.claimCheckOut(claim);
        assertThat(retrieved.body()).isEqualTo("very large payload...");
    }
}
