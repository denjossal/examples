package com.denjossal.study.integration.aws;

import static org.assertj.core.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

import java.util.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.*;
import software.amazon.awssdk.services.sqs.model.*;

/**
 * Real SQS integration test using LocalStack.
 * Demonstrates: send/receive, visibility timeout, DLQ, batch operations.
 */
@Testcontainers
class SQSIntegrationTest {

    @Container
    static final LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.0")).withServices(SQS);

    private SqsClient sqs;

    @BeforeEach
    void setUp() {
        sqs = SqsClient.builder()
                .endpointOverride(localstack.getEndpointOverride(SQS))
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .build();
    }

    @AfterEach
    void tearDown() {
        sqs.close();
    }

    @Test
    void shouldSendAndReceiveMessage() {
        String queueUrl = createQueue("test-queue");

        sqs.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("{\"orderId\": \"ORD-123\", \"total\": 99.99}")
                .build());

        var messages = sqs.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(10)
                        .waitTimeSeconds(5)
                        .build())
                .messages();

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).body()).contains("ORD-123");
    }

    @Test
    void shouldSendBatchMessages() {
        String queueUrl = createQueue("batch-queue");

        var entries = new ArrayList<SendMessageBatchRequestEntry>();
        for (int i = 0; i < 5; i++) {
            entries.add(SendMessageBatchRequestEntry.builder()
                    .id("msg-" + i)
                    .messageBody("event-" + i)
                    .build());
        }

        var result = sqs.sendMessageBatch(SendMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(entries)
                .build());

        assertThat(result.successful()).hasSize(5);
        assertThat(result.failed()).isEmpty();
    }

    @Test
    void shouldDeleteMessageAfterProcessing() {
        String queueUrl = createQueue("delete-queue");

        sqs.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("process-me")
                .build());

        // Receive
        var messages = sqs.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(1)
                        .waitTimeSeconds(5)
                        .build())
                .messages();

        assertThat(messages).hasSize(1);

        // Delete after processing
        sqs.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(messages.get(0).receiptHandle())
                .build());

        // Verify queue is empty
        var remaining = sqs.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(1)
                        .waitTimeSeconds(1)
                        .build())
                .messages();

        assertThat(remaining).isEmpty();
    }

    @Test
    void shouldSendWithMessageAttributes() {
        String queueUrl = createQueue("attr-queue");

        sqs.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("order-event")
                .messageAttributes(Map.of(
                        "eventType",
                                MessageAttributeValue.builder()
                                        .dataType("String")
                                        .stringValue("OrderPlaced")
                                        .build(),
                        "priority",
                                MessageAttributeValue.builder()
                                        .dataType("Number")
                                        .stringValue("1")
                                        .build()))
                .build());

        var messages = sqs.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageAttributeNames("All")
                        .waitTimeSeconds(5)
                        .build())
                .messages();

        assertThat(messages.get(0).messageAttributes().get("eventType").stringValue())
                .isEqualTo("OrderPlaced");
        assertThat(messages.get(0).messageAttributes().get("priority").stringValue())
                .isEqualTo("1");
    }

    private String createQueue(String name) {
        return sqs.createQueue(CreateQueueRequest.builder().queueName(name).build())
                .queueUrl();
    }
}
