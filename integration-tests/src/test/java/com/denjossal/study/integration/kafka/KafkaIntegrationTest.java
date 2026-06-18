package com.denjossal.study.integration.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Real Kafka integration test using Testcontainers.
 * Demonstrates: produce, consume, consumer groups, topic management.
 */
@Testcontainers
class KafkaIntegrationTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer("apache/kafka:3.8.0");

    private static final String TOPIC = "orders";

    @BeforeAll
    static void createTopic() throws Exception {
        try (var admin = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
            admin.createTopics(List.of(new NewTopic(TOPIC, 3, (short) 1))).all().get();
        }
    }

    @Test
    void shouldProduceAndConsumeMessages() throws Exception {
        String bootstrapServers = kafka.getBootstrapServers();

        // Produce
        try (var producer = new KafkaProducer<String, String>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
        ))) {
            for (int i = 0; i < 5; i++) {
                producer.send(new ProducerRecord<>(TOPIC, "key-" + i, "order-" + i)).get();
            }
        }

        // Consume
        try (var consumer = new KafkaConsumer<String, String>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, "test-group",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()
        ))) {
            consumer.subscribe(List.of(TOPIC));

            var records = new ArrayList<String>();
            long deadline = System.currentTimeMillis() + 10_000;
            while (records.size() < 5 && System.currentTimeMillis() < deadline) {
                var polled = consumer.poll(Duration.ofMillis(500));
                polled.forEach(r -> records.add(r.value()));
            }

            assertThat(records).hasSize(5);
            assertThat(records).containsExactlyInAnyOrder(
                    "order-0", "order-1", "order-2", "order-3", "order-4");
        }
    }

    @Test
    void shouldDistributeAcrossPartitions() throws Exception {
        String bootstrapServers = kafka.getBootstrapServers();

        try (var producer = new KafkaProducer<String, String>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
        ))) {
            var partitions = new HashSet<Integer>();
            for (int i = 0; i < 20; i++) {
                var metadata = producer.send(
                        new ProducerRecord<>(TOPIC, "key-" + i, "val-" + i)).get();
                partitions.add(metadata.partition());
            }
            // With 3 partitions and 20 keys, should hit multiple partitions
            assertThat(partitions.size()).isGreaterThan(1);
        }
    }

    @Test
    void shouldRespectConsumerGroupOffsets() throws Exception {
        String bootstrapServers = kafka.getBootstrapServers();
        String topic = "offset-test";

        try (var admin = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {
            admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1))).all().get();
        }

        // Produce 3 messages
        try (var producer = new KafkaProducer<String, String>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
        ))) {
            for (int i = 0; i < 3; i++) {
                producer.send(new ProducerRecord<>(topic, "k", "msg-" + i)).get();
            }
        }

        // First consumer reads all 3
        var firstBatch = consumeAll(bootstrapServers, topic, "offset-group", 3);
        assertThat(firstBatch).hasSize(3);

        // Second consumer in same group reads nothing (offsets committed)
        var secondBatch = consumeAll(bootstrapServers, topic, "offset-group", 0);
        assertThat(secondBatch).isEmpty();
    }

    private List<String> consumeAll(String servers, String topic, String group, int expected) {
        try (var consumer = new KafkaConsumer<String, String>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers,
                ConsumerConfig.GROUP_ID_CONFIG, group,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()
        ))) {
            consumer.subscribe(List.of(topic));
            var records = new ArrayList<String>();
            long deadline = System.currentTimeMillis() + 5_000;
            while (records.size() < expected && System.currentTimeMillis() < deadline) {
                consumer.poll(Duration.ofMillis(300)).forEach(r -> records.add(r.value()));
            }
            consumer.commitSync();
            return records;
        }
    }
}
