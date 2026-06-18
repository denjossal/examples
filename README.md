# Java Study & Portfolio

[![CI](https://github.com/denjossal/examples/actions/workflows/ci.yml/badge.svg)](https://github.com/denjossal/examples/actions/workflows/ci.yml)

A multi-module Maven project for structured Java learning, organized by topic. Each
module has its own README with details and tradeoffs.

## Highlights

If you're skimming, start here:

- **[Distributed lock](./integration-tests)** — Redis `SET NX EX` acquisition with a
  Lua token-release script, verified under concurrent contention with Testcontainers.
- **[Outbox + CDC](./spring-boot)** — transactional outbox with a polling publisher,
  exercised end-to-end against real PostgreSQL + Kafka in
  [integration-tests](./integration-tests).
- **[Resilience patterns](./spring-boot)** — circuit breaker, bulkhead, and retry with
  backoff, with SLF4J logging of state transitions.
- **[Real integration tests](./integration-tests)** — Testcontainers spinning up Kafka,
  PostgreSQL, Redis, and LocalStack (DynamoDB/SQS/S3) instead of mocks.

## Modules

| Module | Description | Status |
|--------|-------------|--------|
| [dsa](./dsa) | Data structures from scratch + Big O analysis | Done |
| [algorithms](./algorithms) | LeetCode solutions organized by pattern (13 patterns, 28 problems) | Done |
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

## Code Quality

- **CI** — GitHub Actions builds + unit-tests on JDK 25, runs the Testcontainers
  integration suite, and checks formatting (see [`.github/workflows/ci.yml`](./.github/workflows/ci.yml)).
- **Coverage** — JaCoCo reports per module under `target/site/jacoco/`.
- **Formatting** — Spotless with the Palantir Java Format, run under a `lint` profile on
  JDK 21 (the formatter is not yet compatible with JDK 25):

  ```bash
  sdk use java 21.0.8-amzn
  mvn -Plint spotless:check   # verify
  mvn -Plint spotless:apply   # auto-format
  ```

  The `java-modern` module and `aws/.../LambdaColdStartOptimization.java` are excluded
  because they use Java 25 preview syntax the formatter can't parse.

## Building

The project targets Java 21/25, so **run Maven on JDK 25**. On macOS the integration
tests also need a Docker daemon. The committed build no longer hardcodes any
machine-specific path — local Docker/JDK config lives in `scripts/local-env.sh`.

```bash
# One-time per shell: select JDK 25 + point Testcontainers at Colima
source scripts/local-env.sh

# Run all unit tests (no Docker required)
mvn clean test -DskipIntegrationTests=true

# Run everything including integration tests (requires Docker)
colima start
mvn clean verify
```

## Docker Setup (for integration tests)

Integration tests use [Testcontainers](https://testcontainers.com/) and require a running Docker daemon.

```bash
# Start Colima (Docker runtime for macOS)
colima start

# Load JDK 25 + Docker socket env (sets DOCKER_HOST + the Ryuk socket override)
source scripts/local-env.sh

# Run only integration tests
mvn test -pl integration-tests

# Skip integration tests in full build
mvn test -DskipIntegrationTests=true
```

Why the env vars (see `scripts/local-env.sh`): Colima exposes the Docker socket at
`$HOME/.colima/default/docker.sock`, so `DOCKER_HOST` points there, and
`TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock` tells the Ryuk reaper
which path to bind-mount inside containers. CI needs none of this — GitHub-hosted
Linux runners expose Docker at the default socket automatically.

## Roadmap

See [reskilling-roadmap-v3-balanced.md](./reskilling-roadmap-v3-balanced.md) for the full learning plan.
