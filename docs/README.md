# Docs — Learning & Diagrams

Visual, diagram-first explanations of the patterns implemented across this repo, plus curated
external reading for each topic. Where a diagram explains real code, it links straight to the
class (and its test).

> **Rendering:** every diagram is [Mermaid](https://mermaid.js.org/) inside a fenced
> ` ```mermaid ` block — GitHub renders these natively in the browser. In an IDE, install a
> Mermaid preview plugin (VS Code, IntelliJ) to see them locally.

## Topics

| Doc | What's inside |
|-----|---------------|
| [Distributed-systems patterns](./distributed-systems-patterns.md) | Circuit breaker, saga + compensation, transactional outbox, EIP, distributed lock |
| [DSA & algorithms](./dsa-and-algorithms.md) | BST traversal, BFS vs DFS, HashMap chaining, an algorithm-pattern decision tree |
| [Spring Boot patterns](./spring-boot-patterns.md) | Layered architecture, DI/IoC, bean lifecycle, request + error flow, event listeners |
| [GoF design patterns](./gof-design-patterns.md) | Strategy, Observer, State, Factory Method, Decorator |

## Diagram-type legend

The docs pick a Mermaid diagram type to match what's being shown:

| Type | Used for |
|------|----------|
| `stateDiagram-v2` | State machines — circuit-breaker states, bean lifecycle |
| `sequenceDiagram` | Time-ordered interactions — saga, outbox poller, request/error flow |
| `flowchart` / `graph` | Structure & decisions — layered architecture, HashMap buckets, pattern decision tree |
| `classDiagram` | Class relationships — the GoF patterns |

These docs are reference material only — they add **no build dependency** (`docs/` is not a
Maven module, so CI and the build are unaffected).
