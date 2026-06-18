# java-modern

Java 8→25 language and library evolution — runnable demos of each major feature, grouped by era and topic.

## Packages

### `evolution` — feature timeline
A class per era, each method annotated with the version that introduced the feature shown.

- **`Java8Features`** — lambdas, Stream API, `Optional`, method references, default methods (Java 8)
- **`Java9To11Features`** — `List/Set/Map.of()`, `takeWhile`/`dropWhile`, 3-arg `Stream.iterate`, `Optional.ifPresentOrElse`/`or` (9), `var` + unmodifiable collectors (10), `String.isBlank`/`lines`/`repeat`/`strip`, `var` in lambdas (11)
- **`Java12To16Features`** — switch expressions + `Collectors.teeing` (12/14), text blocks (13/15), records (14→16), `instanceof` pattern matching + `Stream.toList()` (16)
- **`Java17To21Features`** — sealed classes (17), pattern matching for `switch` with guarded `when`, record patterns (nested deconstruction), virtual threads, sequenced collections (21)
- **`Java22To25Features`** — unnamed variables `_` (22), statements before `super()` (24), `StructuredTaskScope` (25 final)

### `features` — sealed types + pattern matching
- **`SealedInterfaceDemo`** — sealed `Shape` hierarchy with exhaustive `switch` (no default needed) — sealed interfaces (17), pattern matching for switch (21)
- **`RecordPatternDemo`** — recursive `Expr` tree evaluated via `instanceof` patterns (16)

### `streams` — Stream API depth
- **`StreamAdvanced`** — `groupingBy` + `flatMapping`, `partitioningBy`, `teeing` (12), `mapMulti` (16), parallel `reduce` with combiner
- **`StreamPerformance`** — for-loop vs `parallelStream()` on CPU-bound work, with timing
- **`User`** — supporting record

### `concurrency`
- **`VirtualThreadDemo`** — sequential vs thread-pool I/O comparison, `CompletableFuture` composition (`thenCombine`)

### Stream Gatherers (Java 24, JEP 485)
Shown in `Java22To25Features`: `Gatherers.windowFixed`, `windowSliding`, `fold`, `scan`, and `mapConcurrent` (bounded-concurrency parallel map) via `stream().gather(...)`.

## Build note

This module compiles with `--enable-preview` and uses **Java 25 preview syntax** (e.g. unnamed variables `_`, `StructuredTaskScope.open()`). Because of this, the **Spotless formatter (which runs on JDK 21) is skipped for this module** — its preview syntax does not parse under the formatter's JDK. Build it with JDK 25.
