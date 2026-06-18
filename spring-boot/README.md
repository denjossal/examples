# spring-boot

Microservices and distributed-systems patterns implemented from scratch (in-memory, no frameworks) so the mechanics — and their tradeoffs — are visible. Each pattern's class comment names the production tool you'd reach for instead.

## Building

```bash
mvn -pl spring-boot test
```

Pure unit tests — no Docker required. (The real-infra versions of these patterns live in [`integration-tests`](../integration-tests).)

## Packages

### `api` — REST design
- **`UserController`** — resource-oriented CRUD on `/api/v1/users` with correct status codes (201 + `Location` on create, 404 on miss, 204 on delete). Backed by a `ConcurrentHashMap`, so it's a teaching stub, not persistent.
- **`GlobalExceptionHandler`** — centralizes errors as RFC-7807 `ProblemDetail` responses (Spring 6+). Why: clients get one machine-readable error shape (`type`/`title`/`status`/`detail`) instead of ad-hoc JSON.

### `integration` — Enterprise Integration Patterns
`EnterpriseIntegrationPatterns` implements the Hohpe & Woolf vocabulary as pure functions over a `Message(headers, body)` record: **Content-Based Router** (with dead-letter fallback), **Splitter**, **Aggregator**, **Wire Tap**, and **Claim Check**.
- *What & why:* the building blocks Apache Camel routes are composed from; mental model is `From → Process → To`.
- *Tradeoffs:* Claim Check trades message size for an external store lookup (and a dangling-claim failure mode); Wire Tap adds observability at the cost of a duplicated message.

### `messaging` — event-driven patterns
- **`Event<T>`** — base record: `eventId`, `type`, `source`, `timestamp`, `payload`. The `eventId` is what makes idempotency possible.
- **`InMemoryEventBus`** — pub/sub that decouples producers from consumers; unroutable or handler-throwing events go to a dead-letter queue. *Tradeoff:* synchronous, in-process, no durability — production would use Kafka, SNS/SQS, EventBridge, or Spring's `ApplicationEventPublisher`.
- **`IdempotentConsumer<T>`** — dedupes by tracking processed `eventId`s. *Why:* at-least-once delivery means duplicates **will** arrive. *Tradeoff:* the processed-id set is in-memory here and grows unbounded; production needs a shared, TTL'd store (Redis/DB).
- **`OutboxPattern`** — writes a business change and an outbox entry "in the same transaction," then a poller publishes unpublished entries. *Why:* DB-write + broker-publish isn't atomic, so doing either first risks a lost event or an orphan event. *Tradeoffs:* gives **at-least-once** delivery (consumers must be idempotent) and adds **polling latency**; production swaps the poller for CDC (Debezium).

### `observability` — `DistributedTracing`
Models the three pillars (metrics, traces, logs) and OpenTelemetry concepts — `Trace`, `Span` (with parent/child + tags + `OK`/`ERROR` status), and context propagation. Helpers find the slowest span and error spans in a trace. *Tradeoff:* this is a hand-rolled tracer; production uses the OTEL SDK + Collector exporting to Jaeger/Dynatrace, and tracing adds per-span overhead (usually sampled).

### `performance` — `PerformanceTuning`
Concrete tuning levers: chunked streaming (never buffer the whole dataset), thread-pool sizing (`cores+1` for CPU-bound vs. a utilization formula for I/O-bound), sequential-vs-parallel stream tradeoffs, an LRU `ComputeCache` with hit-rate tracking, and an allocation-pressure demo (`joinBad` → `joinGood` → `joinBest`). *Rule that drives all of it:* measure first; parallelism/caching help only for the right workload and can hurt otherwise.

### `resilience` — failure-handling patterns (now with SLF4J logging)
All three log state transitions and failures via SLF4J so behavior is observable in tests and prod.
- **`CircuitBreaker<T>`** — states **CLOSED** (pass through, count failures) → **OPEN** (short-circuit to fallback once failures hit the threshold) → **HALF_OPEN** (after the open window, let a probe through) → back to CLOSED on success or OPEN on failure. *Why:* stop hammering a failing dependency and cascading the failure upstream. *Tradeoff:* a too-low threshold trips on transient blips; a too-high one delays protection.
- **`Bulkhead<T>`** — caps concurrency with a `Semaphore`, rejecting (after a timeout) once permits are exhausted. *Why:* one slow dependency can't drain the whole thread pool. *Tradeoff:* **isolation vs. throughput** — a smaller bulkhead isolates better but rejects more under load.
- **`RetryWithBackoff`** — retries up to `maxAttempts` with exponential delay (`delay *= multiplier`). *Why:* ride out transient failures. *Tradeoffs:* only safe for idempotent operations; backoff needs jitter to avoid a thundering herd; exhausting retries throws `RetryExhaustedException`.

*Production note (stated in each class):* these are teaching implementations — real systems use **Resilience4j** (circuit breaker, bulkhead, retry) or Spring Retry rather than these from-scratch versions.
