package com.denjossal.study.aws.events;

import com.denjossal.study.aws.events.EventBridgePattern.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class EventBridgePatternTest {

    @Test
    void shouldRouteEventToMatchingTargets() {
        var eb = EventBridgePattern.ecommerceSetup();

        var event = CloudEvent.of("order-service", "OrderPlaced",
                Map.of("orderId", "ORD-1", "amount", 50.0));

        var targets = eb.putEvent(event);

        assertThat(targets).containsExactlyInAnyOrder("fulfillment-lambda", "analytics-firehose");
    }

    @Test
    void shouldFanOutToMultipleTargets() {
        var eb = EventBridgePattern.ecommerceSetup();

        // High-value order triggers both order-processing AND fraud-detection rules
        var event = CloudEvent.of("order-service", "OrderPlaced",
                Map.of("orderId", "ORD-2", "amount", 5000.0));

        var targets = eb.putEvent(event);

        assertThat(targets).contains("fulfillment-lambda", "analytics-firehose", "fraud-step-function");
    }

    @Test
    void shouldRoutePaymentEvents() {
        var eb = EventBridgePattern.ecommerceSetup();

        var event = CloudEvent.of("payment-service", "PaymentCompleted",
                Map.of("paymentId", "PAY-1"));

        var targets = eb.putEvent(event);

        assertThat(targets).containsExactly("notification-lambda");
    }

    @Test
    void shouldNotRouteUnmatchedEvents() {
        var eb = EventBridgePattern.ecommerceSetup();

        var event = CloudEvent.of("unknown-service", "SomethingHappened", Map.of());

        var targets = eb.putEvent(event);

        assertThat(targets).isEmpty();
    }

    @Test
    void shouldAccumulateEventsInTargetQueues() {
        var eb = EventBridgePattern.ecommerceSetup();

        eb.putEvent(CloudEvent.of("order-service", "OrderPlaced", Map.of("orderId", "1", "amount", 10)));
        eb.putEvent(CloudEvent.of("order-service", "OrderPlaced", Map.of("orderId", "2", "amount", 20)));

        assertThat(eb.getEventsForTarget("fulfillment-lambda")).hasSize(2);
    }
}
