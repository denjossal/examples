# Java Study & Portfolio

A multi-module Maven project for structured Java learning, organized by topic.

## Modules

| Module | Description | Status |
|--------|-------------|--------|
| [dsa](./dsa) | Data structures from scratch + Big O analysis | Done |
| [algorithms](./algorithms) | LeetCode solutions organized by pattern (12 patterns, 22 problems) | Done |
| [java-modern](./java-modern) | Java 8→25 evolution, virtual threads, Stream Gatherers, sealed types | Done |
| [spring-boot](./spring-boot) | Microservices patterns, resilience, EIP, observability, performance | Done |
| [ai-sdlc](./ai-sdlc) | AI-SDLC playbook: SDD, prompt engineering, agent orchestration | Done |
| [aws](./aws) | AWS serverless: Lambda, EventBridge, DynamoDB single-table design | Done |
| [integration-tests](./integration-tests) | Real integration tests with Testcontainers (Kafka, DynamoDB, SQS, PostgreSQL) | Done |

## Tech Stack

- Java 25 (with --enable-preview)
- Spring Boot 3.4.x (compiled to Java 21)
- JUnit 5 + AssertJ
- Maven (multi-module)
- Testcontainers (Kafka, LocalStack, PostgreSQL)
- AWS SDK v2 (DynamoDB, SQS, S3)
- Apache Kafka Client

## Building

```bash
# Run all unit tests (no Docker required)
mvn clean test -DskipIntegrationTests=true

# Run everything including integration tests (requires Docker)
colima start
mvn clean test
```

## Docker Setup (for integration tests)

Integration tests use [Testcontainers](https://testcontainers.com/) and require a running Docker daemon.

```bash
# Start Colima (Docker runtime for macOS)
colima start

# Run only integration tests
mvn test -pl integration-tests

# Skip integration tests in full build
mvn test -DskipIntegrationTests=true
```

The Docker socket is configured at: `unix:///Users/Dennis_Salcedo/.colima/default/docker.sock`

## Roadmap

See [reskilling-roadmap-v3-balanced.md](./reskilling-roadmap-v3-balanced.md) for the full learning plan.
