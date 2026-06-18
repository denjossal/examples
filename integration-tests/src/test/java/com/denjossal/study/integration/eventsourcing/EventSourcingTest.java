package com.denjossal.study.integration.eventsourcing;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Event Sourcing — store state as a sequence of events, rebuild by replaying.
 *
 * Instead of storing current state (CRUD), store every change as an immutable event.
 * Current state = replay all events from the beginning.
 *
 * Benefits:
 * - Complete audit trail
 * - Time-travel (rebuild state at any point in time)
 * - Event replay for new projections
 * - Natural fit for event-driven architectures
 *
 * This test uses Kafka as the event store (log-compacted topic).
 * In production: EventStoreDB, or Kafka with log compaction.
 */
@Testcontainers
class EventSourcingTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer("apache/kafka:3.8.0");

    private static final String EVENT_STORE_TOPIC = "bank-account-events";

    @BeforeAll
    static void createTopic() throws Exception {
        try (var admin = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
            admin.createTopics(List.of(new NewTopic(EVENT_STORE_TOPIC, 1, (short) 1))).all().get();
        }
    }

    // ─── Domain: Bank Account ───────────────────────────────────────────────

    sealed interface AccountEvent permits AccountCreated, MoneyDeposited, MoneyWithdrawn {}
    record AccountCreated(String accountId, String owner) implements AccountEvent {}
    record MoneyDeposited(String accountId, double amount) implements AccountEvent {}
    record MoneyWithdrawn(String accountId, double amount) implements AccountEvent {}

    record AccountState(String accountId, String owner, double balance, int version) {}

    @Test
    void shouldRebuildStateFromEvents() throws Exception {
        String accountId = "ACC-001";

        // Append events to the event store (Kafka)
        appendEvent(accountId, "AccountCreated", """
                {"accountId":"ACC-001","owner":"Alice"}""");
        appendEvent(accountId, "MoneyDeposited", """
                {"accountId":"ACC-001","amount":1000.0}""");
        appendEvent(accountId, "MoneyDeposited", """
                {"accountId":"ACC-001","amount":500.0}""");
        appendEvent(accountId, "MoneyWithdrawn", """
                {"accountId":"ACC-001","amount":200.0}""");

        // Replay: rebuild current state from all events
        var events = replayEvents(accountId);
        var state = rebuildState(events);

        assertThat(state.accountId()).isEqualTo("ACC-001");
        assertThat(state.owner()).isEqualTo("Alice");
        assertThat(state.balance()).isEqualTo(1300.0); // 1000 + 500 - 200
        assertThat(state.version()).isEqualTo(4);
    }

    @Test
    void shouldReplayToSpecificVersion() throws Exception {
        String accountId = "ACC-002";

        appendEvent(accountId, "AccountCreated", """
                {"accountId":"ACC-002","owner":"Bob"}""");
        appendEvent(accountId, "MoneyDeposited", """
                {"accountId":"ACC-002","amount":2000.0}""");
        appendEvent(accountId, "MoneyWithdrawn", """
                {"accountId":"ACC-002","amount":750.0}""");
        appendEvent(accountId, "MoneyDeposited", """
                {"accountId":"ACC-002","amount":300.0}""");

        // Replay only first 2 events (time-travel to version 2)
        var allEvents = replayEvents(accountId);
        var stateAtV2 = rebuildState(allEvents.subList(0, 2));

        assertThat(stateAtV2.balance()).isEqualTo(2000.0);
        assertThat(stateAtV2.version()).isEqualTo(2);

        // Full replay
        var currentState = rebuildState(allEvents);
        assertThat(currentState.balance()).isEqualTo(1550.0); // 2000 - 750 + 300
        assertThat(currentState.version()).isEqualTo(4);
    }

    @Test
    void shouldMaintainEventOrdering() throws Exception {
        String accountId = "ACC-003";

        for (int i = 1; i <= 10; i++) {
            appendEvent(accountId, "MoneyDeposited", """
                    {"accountId":"ACC-003","amount":%d.0}""".formatted(i * 100));
        }

        var events = replayEvents(accountId);
        assertThat(events).hasSize(10);

        // Events maintain insertion order
        assertThat(events.get(0)).contains("100.0");
        assertThat(events.get(9)).contains("1000.0");
    }

    // ─── Event Store operations ─────────────────────────────────────────────

    private void appendEvent(String aggregateId, String eventType, String payload) throws Exception {
        try (var producer = new KafkaProducer<String, String>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
        ))) {
            var record = new ProducerRecord<>(EVENT_STORE_TOPIC, aggregateId,
                    "{\"type\":\"%s\",\"data\":%s}".formatted(eventType, payload));
            producer.send(record).get();
        }
    }

    private List<String> replayEvents(String aggregateId) {
        try (var consumer = new KafkaConsumer<String, String>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "replay-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()
        ))) {
            consumer.subscribe(List.of(EVENT_STORE_TOPIC));
            var events = new ArrayList<String>();
            long deadline = System.currentTimeMillis() + 10_000;

            while (System.currentTimeMillis() < deadline) {
                var records = consumer.poll(Duration.ofMillis(500));
                for (var record : records) {
                    if (record.key().equals(aggregateId)) {
                        events.add(record.value());
                    }
                }
                if (!records.isEmpty() && events.size() > 0) {
                    // Give a bit more time for stragglers then break
                    var extra = consumer.poll(Duration.ofMillis(500));
                    for (var r : extra) {
                        if (r.key().equals(aggregateId)) events.add(r.value());
                    }
                    break;
                }
            }
            return events;
        }
    }

    private AccountState rebuildState(List<String> events) {
        String accountId = null;
        String owner = null;
        double balance = 0;
        int version = 0;

        for (var eventJson : events) {
            version++;
            if (eventJson.contains("AccountCreated")) {
                accountId = extractField(eventJson, "accountId");
                owner = extractField(eventJson, "owner");
            } else if (eventJson.contains("MoneyDeposited")) {
                if (accountId == null) accountId = extractField(eventJson, "accountId");
                balance += extractAmount(eventJson);
            } else if (eventJson.contains("MoneyWithdrawn")) {
                balance -= extractAmount(eventJson);
            }
        }
        return new AccountState(accountId, owner, balance, version);
    }

    private String extractField(String json, String field) {
        int start = json.indexOf("\"" + field + "\":\"") + field.length() + 4;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private double extractAmount(String json) {
        int start = json.indexOf("\"amount\":") + 9;
        int end = json.indexOf("}", start);
        return Double.parseDouble(json.substring(start, end));
    }
}
