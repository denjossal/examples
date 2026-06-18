# integration-tests

Distributed-systems patterns proven against **real infrastructure** spun up by [Testcontainers](https://testcontainers.com/) — Redis, PostgreSQL, Kafka, and LocalStack (S3/DynamoDB/SQS) — instead of mocks.

## Why Testcontainers (and the tradeoffs)

Mocks prove your code calls an API; they don't prove the API behaves as you assume. These tests exercise real `SET NX EX` semantics, real Postgres transactions, real Kafka offsets, and real S3 multipart rules — so the assertions hold against actual server behavior. **Tradeoff:** each test pulls and boots a Docker container, so the suite is slower and **requires a running Docker daemon** (the trade for fidelity).

## Running locally (macOS)

Tests target **JDK 25** and, on macOS, run Docker via Colima. The build hardcodes no machine paths — local overrides live in `scripts/local-env.sh`:

```bash
colima start                      # Docker runtime for macOS
source scripts/local-env.sh       # JDK 25 + Colima Docker socket env
mvn test -pl integration-tests
```

`scripts/local-env.sh` sets `JAVA_HOME` to JDK 25, points `DOCKER_HOST` at the Colima socket (`~/.colima/default/docker.sock`), and sets `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock` so the Ryuk reaper bind-mounts the right path inside containers.

**CI** runs on GitHub-hosted Linux runners, which expose Docker at the default socket and select the JDK via `setup-java` — so CI needs **none** of these overrides.

Skip the slow integration tests in a full build:

```bash
mvn test -DskipIntegrationTests=true
```

## What's tested

### `distributed` — coordination primitives (Redis / Postgres + Kafka)
- **`DistributedLockTest`** — mutual exclusion across instances via `SET key token NX EX ttl`: `NX` makes acquire atomic, `EX` auto-expires so a crashed holder doesn't deadlock, and a unique token + a **Lua check-and-delete** ensures only the holder releases (no GET/DEL race). Includes a 10-thread × 50-increment concurrency proof. *Production:* Redlock/Redisson.
- **`RateLimiterTest`** — three algorithms: **fixed window** (`INCR`+`EXPIRE`, simple but bursty at boundaries), **sliding-window log** (sorted set of timestamps, accurate but more memory), and **token bucket** (refills over time, allows controlled bursts). Sliding-window and token-bucket logic run as Lua for atomicity.
- **`OutboxPollerTest`** — the real transactional outbox: writes order + outbox row in **one Postgres transaction**, then a poller reads unpublished rows, publishes to **Kafka**, and marks them published. Proves atomic write, end-to-end delivery, and idempotent re-poll. *Tradeoff:* **at-least-once** + **polling latency**; production uses Debezium CDC.

### `streaming` — `S3StreamingTest` (LocalStack S3)
Processes large objects without buffering the whole file: streaming line-by-line over a 10k-row CSV, per-partition chunked processing, and a real **multipart upload** (3 × 5 MB parts, honoring S3's part-size minimum). *Why:* keeps memory flat regardless of object size.

### `springboot` — full service, real infra
- **`OrderServiceIntegrationTest`** — boots the `OrderApplication` (REST + JPA + Spring Kafka) against real **PostgreSQL + Kafka**: place/confirm/cancel an order, persist it, publish lifecycle events to Kafka, and have the `InventoryEventListener` (`@KafkaListener`) consume them. Covers the PENDING → CONFIRMED / CANCELLED lifecycle and query-by-customer.

### Infra fundamentals
- **`KafkaIntegrationTest`** — produce/consume, partition distribution, consumer-group offset semantics.
- **`PostgreSQLIntegrationTest`** — schema, CRUD, joins, unique constraints, transaction rollback.
- **`DynamoDBIntegrationTest`** (LocalStack) — single-table design with AWS SDK v2: put/get, query by partition key, sort-key prefix, conditional update.
- **`SQSIntegrationTest`** (LocalStack) — send/receive, batch send, message attributes, delete-after-process.

### Architectural patterns (composed on the above)
- **`CacheAsidePatternTest`** (Redis + Postgres) — lazy-load on miss, invalidate on write. *Tradeoff:* miss latency, possible staleness, cache stampede on cold start.
- **`CQRSPatternTest`** (Postgres write side, Redis read side) — project writes into a denormalized read model. *Tradeoff:* eventual consistency between models.
- **`EventSourcingTest`** (Kafka as event store) — store events, rebuild state by replay, replay to a specific version, preserve ordering.
- **`SagaPatternTest`** (Kafka + Postgres) — order-placement saga with compensating transactions on payment failure (release inventory, cancel order).
