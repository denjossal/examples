package com.denjossal.study.springboot.integration;

import java.util.*;
import java.util.function.*;

/**
 * Enterprise Integration Patterns (EIP) — foundation vocabulary.
 *
 * Core patterns from Hohpe & Woolf, as implemented in Apache Camel:
 * - Content-Based Router: route messages by content
 * - Splitter: break composite message into parts
 * - Aggregator: combine multiple messages into one
 * - Dead Letter Channel: handle failed messages
 * - Wire Tap: copy message for monitoring without affecting flow
 *
 * Mental model for Camel: Route = From(endpoint) → Process → To(endpoint)
 * Exchange carries the message through the route with headers + body.
 */
public class EnterpriseIntegrationPatterns {

    public record Message(Map<String, String> headers, String body) {
        public static Message of(String body) {
            return new Message(new HashMap<>(), body);
        }

        public static Message of(String body, String headerKey, String headerValue) {
            var headers = new HashMap<String, String>();
            headers.put(headerKey, headerValue);
            return new Message(headers, body);
        }
    }

    // ─── Content-Based Router ───────────────────────────────────────────────

    public static <T> List<Message> contentBasedRouter(
            List<Message> messages,
            Function<Message, String> classifier,
            Map<String, Consumer<Message>> routes,
            Consumer<Message> deadLetter) {

        var routed = new ArrayList<Message>();
        for (var msg : messages) {
            String key = classifier.apply(msg);
            var handler = routes.get(key);
            if (handler != null) {
                handler.accept(msg);
                routed.add(msg);
            } else {
                deadLetter.accept(msg);
            }
        }
        return routed;
    }

    // ─── Splitter ───────────────────────────────────────────────────────────

    public static List<Message> splitter(Message compositeMessage, String delimiter) {
        return Arrays.stream(compositeMessage.body().split(delimiter))
                .map(part -> new Message(new HashMap<>(compositeMessage.headers()), part.trim()))
                .filter(msg -> !msg.body().isEmpty())
                .toList();
    }

    // ─── Aggregator ─────────────────────────────────────────────────────────

    public static Message aggregator(List<Message> messages, String separator) {
        var combinedBody = messages.stream()
                .map(Message::body)
                .reduce((a, b) -> a + separator + b)
                .orElse("");

        var mergedHeaders = new HashMap<String, String>();
        messages.forEach(msg -> mergedHeaders.putAll(msg.headers()));
        mergedHeaders.put("aggregatedCount", String.valueOf(messages.size()));

        return new Message(mergedHeaders, combinedBody);
    }

    // ─── Wire Tap ───────────────────────────────────────────────────────────

    public static Message wireTap(Message message, Consumer<Message> tapConsumer) {
        tapConsumer.accept(new Message(new HashMap<>(message.headers()), message.body()));
        return message;
    }

    // ─── Claim Check ────────────────────────────────────────────────────────

    private static final Map<String, String> claimStore = new HashMap<>();

    public static Message claimCheckIn(Message largeMessage) {
        String claimId = UUID.randomUUID().toString();
        claimStore.put(claimId, largeMessage.body());
        var headers = new HashMap<>(largeMessage.headers());
        headers.put("claimId", claimId);
        return new Message(headers, "CLAIM:" + claimId);
    }

    public static Message claimCheckOut(Message claimMessage) {
        String claimId = claimMessage.headers().get("claimId");
        String body = claimStore.remove(claimId);
        if (body == null) throw new IllegalStateException("Claim not found: " + claimId);
        return new Message(new HashMap<>(claimMessage.headers()), body);
    }
}
