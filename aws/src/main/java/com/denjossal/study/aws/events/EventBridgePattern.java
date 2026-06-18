package com.denjossal.study.aws.events;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

/**
 * EventBridge pattern — event-driven choreography between microservices.
 *
 * EventBridge concepts:
 * - Event Bus: central router for events
 * - Rules: filter events by pattern (source, detail-type, content)
 * - Targets: where matching events are sent (Lambda, SQS, Step Functions)
 *
 * Patterns demonstrated:
 * - Event choreography (vs orchestration)
 * - Content-based routing
 * - Fan-out (one event → multiple targets)
 */
public class EventBridgePattern {

    public record CloudEvent(
            String source,
            String detailType,
            Map<String, Object> detail,
            Instant time
    ) {
        public static CloudEvent of(String source, String detailType, Map<String, Object> detail) {
            return new CloudEvent(source, detailType, detail, Instant.now());
        }
    }

    public record Rule(String name, Predicate<CloudEvent> pattern, List<String> targets) {}

    private final List<Rule> rules = new ArrayList<>();
    private final Map<String, List<CloudEvent>> targetQueues = new LinkedHashMap<>();

    public void addRule(String name, Predicate<CloudEvent> pattern, List<String> targets) {
        rules.add(new Rule(name, pattern, targets));
        for (var target : targets) {
            targetQueues.putIfAbsent(target, new ArrayList<>());
        }
    }

    /**
     * Routes an event through all matching rules to their targets.
     * Returns the list of targets that received the event.
     */
    public List<String> putEvent(CloudEvent event) {
        var matchedTargets = new ArrayList<String>();

        for (var rule : rules) {
            if (rule.pattern().test(event)) {
                for (var target : rule.targets()) {
                    targetQueues.get(target).add(event);
                    matchedTargets.add(target);
                }
            }
        }
        return matchedTargets;
    }

    public List<CloudEvent> getEventsForTarget(String target) {
        return targetQueues.getOrDefault(target, List.of());
    }

    /**
     * Example: e-commerce event routing.
     */
    public static EventBridgePattern ecommerceSetup() {
        var eb = new EventBridgePattern();

        // Order events → fulfillment + analytics
        eb.addRule("order-processing",
                event -> event.source().equals("order-service") && event.detailType().equals("OrderPlaced"),
                List.of("fulfillment-lambda", "analytics-firehose")
        );

        // Payment events → notification service
        eb.addRule("payment-notifications",
                event -> event.source().equals("payment-service"),
                List.of("notification-lambda")
        );

        // High-value orders → fraud detection
        eb.addRule("fraud-detection",
                event -> {
                    var amount = event.detail().getOrDefault("amount", 0);
                    return amount instanceof Number n && n.doubleValue() > 1000;
                },
                List.of("fraud-step-function")
        );

        return eb;
    }
}
