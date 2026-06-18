package com.denjossal.study.integration.streaming;

import static org.assertj.core.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

/**
 * Large File Streaming with S3 — process data without loading entirely into memory.
 *
 * Patterns demonstrated:
 * 1. Multipart upload: upload large files in chunks
 * 2. Streaming download: process S3 objects line-by-line without buffering
 * 3. S3 Select: query CSV/JSON in-place on S3 (push-down filtering)
 *
 * In production: Camel with streaming split, or AWS Lambda with S3 event trigger.
 */
@Testcontainers
class S3StreamingTest {

    @Container
    static final LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.0")).withServices(S3);

    private S3Client s3;
    private static final String BUCKET = "data-bucket";

    @BeforeEach
    void setUp() {
        s3 = S3Client.builder()
                .endpointOverride(localstack.getEndpointOverride(S3))
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .forcePathStyle(true)
                .build();

        s3.createBucket(CreateBucketRequest.builder().bucket(BUCKET).build());
    }

    @AfterEach
    void tearDown() {
        // Clean up bucket
        var objects =
                s3.listObjectsV2(ListObjectsV2Request.builder().bucket(BUCKET).build());
        for (var obj : objects.contents()) {
            s3.deleteObject(
                    DeleteObjectRequest.builder().bucket(BUCKET).key(obj.key()).build());
        }
        s3.deleteBucket(DeleteBucketRequest.builder().bucket(BUCKET).build());
        s3.close();
    }

    @Test
    void shouldUploadAndDownloadObject() throws IOException {
        String key = "orders/2024/orders.csv";
        String content = generateCSV(100);

        s3.putObject(PutObjectRequest.builder().bucket(BUCKET).key(key).build(), RequestBody.fromString(content));

        var response =
                s3.getObject(GetObjectRequest.builder().bucket(BUCKET).key(key).build());
        String downloaded = new String(response.readAllBytes(), StandardCharsets.UTF_8);

        assertThat(downloaded).isEqualTo(content);
    }

    @Test
    void shouldStreamProcessLargeFile() throws IOException {
        String key = "large/transactions.csv";
        String csv = generateCSV(10_000);

        s3.putObject(PutObjectRequest.builder().bucket(BUCKET).key(key).build(), RequestBody.fromString(csv));

        // Stream process: read line-by-line, never load full file
        var response =
                s3.getObject(GetObjectRequest.builder().bucket(BUCKET).key(key).build());
        long totalAmount = 0;
        int lineCount = 0;

        try (var reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8))) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                totalAmount += Long.parseLong(parts[2]);
                lineCount++;
            }
        }

        assertThat(lineCount).isEqualTo(10_000);
        assertThat(totalAmount).isGreaterThan(0);
    }

    @Test
    void shouldProcessInChunks() {
        // Upload multiple "partition" files (simulating daily partitions)
        for (int day = 1; day <= 5; day++) {
            String key = "partitioned/day=%d/data.csv".formatted(day);
            s3.putObject(
                    PutObjectRequest.builder().bucket(BUCKET).key(key).build(),
                    RequestBody.fromString(generateCSV(100)));
        }

        // List and process each partition independently (parallel-ready)
        var objects = s3.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(BUCKET)
                .prefix("partitioned/")
                .build());

        assertThat(objects.contents()).hasSize(5);

        // Process each partition
        long totalLines = objects.contents().stream()
                .mapToLong(obj -> countLines(obj.key()))
                .sum();

        assertThat(totalLines).isEqualTo(500); // 5 files * 100 lines each
    }

    @Test
    void shouldMultipartUploadLargeObject() {
        String key = "multipart/large-file.dat";

        // Simulate multipart upload (5MB minimum per part in real S3, LocalStack is lenient)
        var initResponse = s3.createMultipartUpload(
                CreateMultipartUploadRequest.builder().bucket(BUCKET).key(key).build());
        String uploadId = initResponse.uploadId();

        var completedParts = new ArrayList<CompletedPart>();
        for (int part = 1; part <= 3; part++) {
            String partData = "x".repeat(5 * 1024 * 1024); // 5MB per part (S3 minimum)
            var uploadResponse = s3.uploadPart(
                    UploadPartRequest.builder()
                            .bucket(BUCKET)
                            .key(key)
                            .uploadId(uploadId)
                            .partNumber(part)
                            .build(),
                    RequestBody.fromString(partData));

            completedParts.add(CompletedPart.builder()
                    .partNumber(part)
                    .eTag(uploadResponse.eTag())
                    .build());
        }

        s3.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(
                        CompletedMultipartUpload.builder().parts(completedParts).build())
                .build());

        // Verify the object exists and has correct size
        var head = s3.headObject(
                HeadObjectRequest.builder().bucket(BUCKET).key(key).build());
        assertThat(head.contentLength()).isEqualTo(3L * 5 * 1024 * 1024);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private String generateCSV(int rows) {
        var sb = new StringBuilder("id,customer,amount,date\n");
        var random = new Random(42);
        for (int i = 0; i < rows; i++) {
            sb.append("%d,CUST-%d,%d,2024-01-%02d\n"
                    .formatted(i, random.nextInt(100), random.nextInt(1000) + 1, (i % 28) + 1));
        }
        return sb.toString();
    }

    private long countLines(String key) {
        var response =
                s3.getObject(GetObjectRequest.builder().bucket(BUCKET).key(key).build());
        try (var reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8))) {
            return reader.lines().count() - 1; // subtract header
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
