package com.denjossal.study.integration.aws;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

/**
 * Real DynamoDB integration test using LocalStack.
 * Demonstrates single-table design with actual AWS SDK v2 calls.
 */
@Testcontainers
class DynamoDBIntegrationTest {

    @Container
    static final LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:4.0"))
            .withServices(DYNAMODB);

    private DynamoDbClient dynamoDb;

    @BeforeEach
    void setUp() {
        dynamoDb = DynamoDbClient.builder()
                .endpointOverride(localstack.getEndpointOverride(DYNAMODB))
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .build();

        dynamoDb.createTable(CreateTableRequest.builder()
                .tableName("app-table")
                .keySchema(
                        KeySchemaElement.builder().attributeName("PK").keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName("SK").keyType(KeyType.RANGE).build()
                )
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("PK").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("SK").attributeType(ScalarAttributeType.S).build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());
    }

    @AfterEach
    void tearDown() {
        dynamoDb.deleteTable(DeleteTableRequest.builder().tableName("app-table").build());
        dynamoDb.close();
    }

    @Test
    void shouldPutAndGetItem() {
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName("app-table")
                .item(Map.of(
                        "PK", AttributeValue.fromS("USER#user-1"),
                        "SK", AttributeValue.fromS("PROFILE"),
                        "name", AttributeValue.fromS("Alice"),
                        "email", AttributeValue.fromS("alice@test.com")
                ))
                .build());

        var response = dynamoDb.getItem(GetItemRequest.builder()
                .tableName("app-table")
                .key(Map.of(
                        "PK", AttributeValue.fromS("USER#user-1"),
                        "SK", AttributeValue.fromS("PROFILE")
                ))
                .build());

        assertThat(response.item().get("name").s()).isEqualTo("Alice");
        assertThat(response.item().get("email").s()).isEqualTo("alice@test.com");
    }

    @Test
    void shouldQueryByPartitionKey() {
        // Create user + 2 orders (single-table pattern)
        dynamoDb.putItem(putRequest("USER#u1", "PROFILE", Map.of("name", "Bob")));
        dynamoDb.putItem(putRequest("USER#u1", "ORDER#001", Map.of("total", "99.99", "status", "PLACED")));
        dynamoDb.putItem(putRequest("USER#u1", "ORDER#002", Map.of("total", "250.00", "status", "SHIPPED")));

        // Query all items for this user
        var response = dynamoDb.query(QueryRequest.builder()
                .tableName("app-table")
                .keyConditionExpression("PK = :pk")
                .expressionAttributeValues(Map.of(":pk", AttributeValue.fromS("USER#u1")))
                .build());

        assertThat(response.items()).hasSize(3);
    }

    @Test
    void shouldQueryWithSortKeyPrefix() {
        dynamoDb.putItem(putRequest("USER#u2", "PROFILE", Map.of("name", "Carol")));
        dynamoDb.putItem(putRequest("USER#u2", "ORDER#001", Map.of("total", "50")));
        dynamoDb.putItem(putRequest("USER#u2", "ORDER#002", Map.of("total", "75")));

        // Query only orders (SK begins_with ORDER#)
        var response = dynamoDb.query(QueryRequest.builder()
                .tableName("app-table")
                .keyConditionExpression("PK = :pk AND begins_with(SK, :sk)")
                .expressionAttributeValues(Map.of(
                        ":pk", AttributeValue.fromS("USER#u2"),
                        ":sk", AttributeValue.fromS("ORDER#")
                ))
                .build());

        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).get("SK").s()).isEqualTo("ORDER#001");
    }

    @Test
    void shouldUpdateItemConditionally() {
        dynamoDb.putItem(putRequest("USER#u3", "ORDER#001",
                Map.of("status", "PLACED", "total", "100")));

        // Update only if status is PLACED (optimistic concurrency)
        dynamoDb.updateItem(UpdateItemRequest.builder()
                .tableName("app-table")
                .key(Map.of(
                        "PK", AttributeValue.fromS("USER#u3"),
                        "SK", AttributeValue.fromS("ORDER#001")
                ))
                .updateExpression("SET #s = :new_status")
                .conditionExpression("#s = :old_status")
                .expressionAttributeNames(Map.of("#s", "status"))
                .expressionAttributeValues(Map.of(
                        ":new_status", AttributeValue.fromS("SHIPPED"),
                        ":old_status", AttributeValue.fromS("PLACED")
                ))
                .build());

        var item = dynamoDb.getItem(GetItemRequest.builder()
                .tableName("app-table")
                .key(Map.of(
                        "PK", AttributeValue.fromS("USER#u3"),
                        "SK", AttributeValue.fromS("ORDER#001")
                ))
                .build()).item();

        assertThat(item.get("status").s()).isEqualTo("SHIPPED");
    }

    private PutItemRequest putRequest(String pk, String sk, Map<String, String> extras) {
        var item = new HashMap<String, AttributeValue>();
        item.put("PK", AttributeValue.fromS(pk));
        item.put("SK", AttributeValue.fromS(sk));
        extras.forEach((k, v) -> item.put(k, AttributeValue.fromS(v)));
        return PutItemRequest.builder().tableName("app-table").item(item).build();
    }
}
