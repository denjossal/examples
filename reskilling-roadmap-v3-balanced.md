# Reskilling Roadmap v3 — Balanced (General Readiness + Role-Targeted, Camel Provisional)

*Created June 2026 · versions verified current as of this date. Supersedes v2. This version re-broadens the plan because the role (Apache Camel especially) isn't yet confirmed.*

## What changed in v3, and why

You flagged three things: add **Big O** and **LeetCode** back, **Apache Camel is still to be discussed**, and for that reason **React is OK to keep**. So v3 re-broadens toward general senior-candidate readiness while keeping the role-specific bets layered on top:

- **Restored as full tracks:** Big O & Complexity · Algorithms & LeetCode (medium-focused) · React (in the optionality tier).
- **Re-scoped:** **Apache Camel → provisional** — build a foundation now (mental model, EIP vocabulary, one small route) so you can speak to it and learn fast, but **defer the deep investment until the role/Camel scope is confirmed**. The EIP knowledge transfers to general integration design either way.
- **Kept:** the role-targeted tracks (AWS serverless/event-driven, microservices/integration design, K8s/AKS, observability, Terraform, performance), the **AI-SDLC** differentiator, and roadmapping/communication.

**The hedge is built in.** Tracks are tiered so the *universal core* pays off no matter how the role lands, and there's an explicit **branch point** where you commit depth based on confirmation.

---

## The two scenarios (decide depth at the branch point)

| | **Scenario A — Camel/integration role confirmed** | **Scenario B — unconfirmed / hedging / different role** |
|---|---|---|
| Go deep on | Apache Camel · AWS serverless/event-driven · K8s/AKS (CSI/PV/PVC) · observability · Terraform · performance | React breadth · general interview readiness (Big O/LeetCode/design) · AWS + microservices at a solid level |
| Keep light | React (optional) | Camel (foundation only) · K8s/Terraform |
| Capstone | **Integration Showcase** (Camel) | **FinDash full-stack** (React + Spring Boot 4 + AWS) |
| Universal either way | Big O · Data Structures · LeetCode · Java/Spring · integration/system design · **AI-SDLC** · roadmapping & comms |

---

## Leverage map (condensed)

Roughly **60–70% there**; gaps concentrated and learnable. ✅ Java 21 · ✅ Spring Boot · ✅ Kafka · ✅ event-driven · ✅✅ **AI tooling / SDD / Claude Code (differentiator)** · ✅ LangGraph/LangChain · ✅ ES/EN comms · ✅ US-West overlap (Bogotá ~2–3h ahead of PT). 🟡 AWS serverless · 🟡 K8s/AKS · 🟡 observability · 🟡 NoSQL · 🟡 integration-design articulation. 🔴 Apache Camel (provisional) · 🔴 large-file streaming · 🔴 Terraform · 🔴 Dynatrace · 🔴 formal roadmapping.

## Target stack (current versions)

**Java 21** · **Spring Boot 4.x** · **Apache Camel 4.18 LTS** (provisional; first Camel for SB4; JDK 17/21) · **React 19** (Vite, TS-first) · **AWS** (Lambda, SQS/SNS/EventBridge, Step Functions, DynamoDB, Aurora/RDS, Bedrock/AgentCore) · **Kubernetes** (EKS/AKS) + **Docker** · **OpenTelemetry**/**Dynatrace** · **Terraform** · **Kafka**.

---

# Tracks

## Foundations (universal — do regardless)

### Track 1 — Big O & Complexity Analysis *(restored, ~1 week, front-loaded)*
- Asymptotic notation (O, Ω, Θ); best/avg/worst case.
- The classes with examples: `O(1)`, `O(log n)`, `O(n)`, `O(n log n)`, `O(n²)`, `O(2ⁿ)`, `O(n!)`.
- Time **and** space; amortized analysis (dynamic array, hash map).
- Analyzing loops, nested loops, recursion (recurrence relations, recursion trees, Master Theorem at a practical level).
- Inferring required complexity from constraints (`n ≤ 10⁵` ⇒ ~`O(n log n)`).
- **Exercises:** annotate ~20 snippets; derive complexity of two of your own Java scripts; predict target complexity from constraints on 10 problems.
- **Resources:** bigocheatsheet.com · NeetCode Big-O explainer · CTCI complexity chapter · MIT 6.006 intros.

### Track 2 — Data Structures in Java *(~2 weeks)*
Implement each from scratch, then learn the JDK class + complexity + idiomatic use + pitfalls: arrays/dynamic arrays · strings · linked lists · stacks/queues (`ArrayDeque`) · hash tables (`HashMap`/`HashSet` internals, `equals`/`hashCode`) · trees (BST, traversals, tries) · heaps (`PriorityQueue`) · graphs (adjacency list) · union-find. **Watch-outs:** autoboxing, `equals`/`hashCode` contract, fail-fast iterators, `Comparable` vs `Comparator`. **Exercise:** a `dsa-java` library with JUnit 5 → first portfolio repo. **Resources:** Sedgewick & Wayne *Algorithms* + Princeton Coursera · Baeldung.

## Core readiness (universal)

### Track 3 — Algorithms & LeetCode (medium-focused) *(restored full track; daily habit)*
**Thesis:** ~10–12 patterns cover the large majority of interviews; medium is the baseline. Master ~15 patterns via canonical problems and learn to *recognize* the pattern.

**Anchor:** **Grind 75** (the medium-weighted core) → extend into **NeetCode 150** (pattern-organized, free videos).

**Recognition cheat-sheet (build this reflex first):**

| Pattern | Trigger | Structure |
|---|---|---|
| Arrays & Hashing | "seen X", counting, dedup, O(1) lookup | HashMap/Set |
| Two Pointers | sorted, pair/triplet to target, palindrome | two indices |
| Sliding Window | contiguous subarray/substring, longest/shortest/≤K | window + counter |
| Binary Search | sorted/monotonic search space, min/max feasible, rotated | lo/hi |
| Stack / Monotonic | matching/nesting, next greater/smaller | stack |
| Linked List (fast/slow) | cycle, middle, reorder | slow/fast |
| Trees (DFS/BFS) | hierarchy, path, depth, level-by-level | recursion/queue |
| Graphs (BFS/DFS) | connectivity, grid, components, unweighted shortest path | adjacency list/queue |
| Heap / Top-K | K largest/smallest/frequent, streaming | PriorityQueue |
| Backtracking | enumerate subsets/permutations/combinations | choose/undo |
| 1-D DP | optimal value, "ways", per-step decision | dp array/memo |
| Intervals | overlapping ranges, merge/insert | sort + sweep |
| Greedy / 2-D DP / Tries / Union-Find | (Tier 3) | — |

**Tier order + ⭐ canonical mediums (do ⭐ first):**
- **Tier 1:** Arrays/Hashing (⭐ Group Anagrams, Top K Frequent, Product of Array Except Self) · Two Pointers (⭐ 3Sum, Container With Most Water) · Sliding Window (⭐ Longest Substring Without Repeating, Longest Repeating Char Replacement) · Binary Search (⭐ Search in Rotated Sorted Array, Find Min in Rotated) · Trees (⭐ Level Order, Validate BST, LCA) · Graphs (⭐ Number of Islands, Clone Graph, Course Schedule).
- **Tier 2:** Stack (⭐ Daily Temperatures) · Linked List (⭐ Reorder List, LRU Cache) · Heap (⭐ Kth Largest, K Closest Points) · Backtracking (⭐ Subsets, Combination Sum, Permutations) · 1-D DP (⭐ House Robber, Coin Change, Longest Increasing Subsequence) · Intervals (⭐ Merge Intervals, Insert Interval).
- **Tier 3:** Greedy (Kadane, Jump Game) · 2-D DP (Unique Paths, LCS) · Tries · Union-Find · Math/Bit.
- **High-freq hards once Tier 1–2 is solid:** Minimum Window Substring · Merge k Sorted Lists · Trapping Rain Water · Find Median from Data Stream.

**Method:** per pattern → learn the template & *why it works* → 1 easy on-ramp → 2–3 mediums under a time cap → re-solve from memory after 3–7 days. Ratio **1 easy : 3 medium : 0–1 hard**. Targets: medium ≤ 30–35 min; recognize the pattern in 2–3 min. **Practice talking out loud** (restate → name pattern + why → complexity → edge cases → code). *(The standalone `algorithms-leetcode-medium-focus.md` has the exhaustive per-pattern problem lists.)*

### Track 4 — Microservices & integration architecture / system design *(universal senior skill)*
Decomposition · sync vs async · messaging patterns · resilience (retries, circuit breakers, idempotency, DLQ, backpressure) · API design/versioning · data consistency (saga/outbox). **Crucially: articulating an integration/system design** on a whiteboard and in a client session — this is the senior-interview substitute for algorithm screens. **Resources:** *Designing Data-Intensive Applications* (Kleppmann) · *Building Microservices* (Newman) · *Enterprise Integration Patterns* · a system-design primer.

### Track 5 — Java 21+ & Spring Boot 4 *(consolidation)*
You're strong; focus the modern/high-value parts: **virtual threads + structured concurrency** (throughput & streaming), Stream API depth, JVM/GC basics, modern language features, the **Spring Boot 3→4 / Framework 7** migration (quick, resume-worthy), Testcontainers, and Spring Boot + Camel together. **Resources:** *Effective Java* · dev.java · Spring guides · Baeldung.

## Differentiator (universal advantage)

### Track 6 — AI-SDLC & AI Champion ⭐ (lead with this)
Package your Nordstrom SDD/Claude-Code work into a client-ready playbook (sanitized):
- **SDD as methodology:** spec → plan → tasks → implementation, with review gates and handoff conventions.
- **AI coding tooling:** Claude Code depth — `CLAUDE.md`/rules, **skills**, **subagents**, **MCP**, hooks (the Claude Agent SDK is the same architecture).
- **SDLC automation:** AI in CI/CD, code review, test/doc generation, PR triage.
- **Adoption / change management:** rolling AI-SDLC into a team migrating from Rust/.NET; enablement, guardrails, measuring impact.
- **AI on AWS:** **Bedrock AgentCore** (runtime, memory, MCP gateway, identity, observability) · **Bedrock Agents** (action groups + Lambda, **Knowledge Bases** for RAG) · **Strands Agents** (AWS-native) · agentic frameworks (**LangGraph** — you know it / **CrewAI** / **Claude Agent SDK**). Crisp mental models: *CrewAI = role-based team; LangGraph = state machine with durable execution + human-in-the-loop.*
- **Deliverable:** the **AI-SDLC Playbook** (below) — your strongest universal portfolio piece.

## Role-specific bets (targeted)

### Track 7 — Apache Camel + Enterprise Integration Patterns 🟡 **PROVISIONAL**
**Foundation now; deep investment only after the role/Camel scope is confirmed.** Camel *is* the EIP catalog in code; your Kafka background transfers directly.
- **Foundation (do now):** the mental model (`CamelContext`, routes, endpoints, components, processors, Exchange), the EIP vocabulary (router, splitter w/ streaming, aggregator, claim check, DLQ), Spring Boot integration (`camel-spring-boot-starter`), and **one small route** via Camel JBang (file → transform → Kafka). Enough to discuss fluently and show trajectory.
- **Deep (Scenario A only):** components depth, error handling/redelivery/idempotency, data transformation/data formats/type converters, **Kafka with Camel** (`camel-kafka`), **large-file streaming** (Split in streaming mode, claim check, backpressure), performance tuning (route concurrency, thread pools, virtual threads on SB4), Kaoto.
- **Resources:** camel.apache.org · *Camel in Action, 2nd ed.* (Ibsen & Anstey) · *Enterprise Integration Patterns* (Hohpe & Woolf).

### Track 8 — AWS serverless & event-driven microservices
Lambda + Java (cold starts, **SnapStart**, GraalVM native — ties to your work) · API Gateway · **SQS/SNS/EventBridge** (event choreography) · **Step Functions** · **DynamoDB** + Aurora/RDS · patterns (event sourcing, CQRS, saga, **outbox**, idempotency, DLQ). *Optional cert: AWS Developer or Solutions Architect Associate.* **Resources:** AWS docs · serverlessland.com · Skill Builder.

### Track 9 — Kubernetes / AKS / EKS + Docker *(CSI, PV/PVC)*
Docker best practices · core objects (Deployments, Services, Ingress, ConfigMaps/Secrets, **StatefulSets**) · **storage: CSI drivers, PV/PVC, StorageClasses** · Helm · health/readiness probes · HPA · running Spring Boot/Camel cloud-natively. Cover K8s generically + **AKS** and **EKS** specifics. *Optional cert: CKAD.* **Resources:** kubernetes.io · *Kubernetes Up & Running* · AKS/EKS docs.

### Track 10 — Observability (OpenTelemetry / Dynatrace)
Three pillars (metrics/traces/logs) · **OTEL** SDK, **auto-instrumentation**, Collector, OTLP · instrument Spring Boot (Micrometer + Tracing) + **Camel routes** · distributed tracing · **Dynatrace** awareness (OneAgent). **Resources:** opentelemetry.io · Spring Boot observability docs · Dynatrace docs.

### Track 11 — IaC with Terraform
Providers, resources, **state + remote state**, modules, workspaces, variables/outputs · Terraform for AWS (and Azure/AKS) · DRY modules · CI/CD integration · `validate`/`plan`, Terratest awareness. *Optional cert: Terraform Associate.* **Resources:** developer.hashicorp.com/terraform · *Terraform Up & Running* (Brikman).

### Track 12 — Performance tuning *(cross-cutting; JD-explicit)*
JVM/GC (G1, ZGC) · **virtual threads** for I/O-bound streaming · Camel route concurrency, thread pools, streaming mode · payload/throughput optimization · large-file streaming (chunking, backpressure, no full-file buffering) · profiling (JFR, async-profiler, Dynatrace) · load testing (k6/Gatling/JMeter). Realized inside Tracks 5, 7, 8.

### Track 13 — SQL / NoSQL *(light)*
Reinforce SQL; add **DynamoDB** (single-table design, access patterns, GSIs); modeling per paradigm and when to use which.

## Breadth / optionality (hedge)

### Track 14 — React, zero to hero *(restored)*
**Prereqs (TS-first):** modern JS (ES2020+), DOM/events/`fetch`, **TypeScript** fundamentals.
- **M1 Fundamentals:** component model, JSX, **Vite** setup, props/composition, lists/keys, conditional rendering, events, controlled inputs.
- **M2 State & hooks:** `useState`, lifting state, `useEffect` (and when not to), `useRef`/`useMemo`/`useCallback`, `useContext`, `useReducer`, custom hooks, rules of hooks.
- **M3 React 19:** Actions, `useActionState`, `useOptimistic`, `use`, Suspense for data, `useTransition`, the React Compiler.
- **M4 Routing & data:** React Router vs framework routing; **TanStack Query**; forms (React Hook Form + Zod).
- **M5 Styling/UI:** Tailwind / CSS Modules; a headless UI lib (Base UI/Radix, shadcn/ui).
- **M6 Framework (Next.js):** App Router, **Server vs Client Components**, server actions, SSR/SSG/ISR.
- **M7 Quality/advanced:** Vitest + React Testing Library, Playwright e2e, performance, a11y, error boundaries, TS-with-React.

**Exercise — FinDash** (grows with the modules, finance-themed, double-duty for the capstone): static UI → state/forms → data (TanStack Query) → routing → forms+validation (`useOptimistic`) → charts (Recharts) → Next.js/RSC → tests. **Resources:** **react.dev** · *The Road to React* (Wieruch) · patterns.dev · TanStack Query / Next.js docs.

## Non-technical (universal for this role type)

### Track 15 — Roadmapping, scope clarification & milestone communication ⭐ (JD: *critical*)
Discovery/scoping (right questions, scope/assumptions/risks/dependencies, MoSCoW, saying no) · roadmap construction (**Now/Next/Later**, themes vs features, milestones, phasing) · executive communication (one-page roadmap, **Amazon "Working Backwards" PR/FAQ**, status/decision comms, managing trade-offs with VP stakeholders). *Meta-note: building and iterating this roadmap with me is itself a worked example you can point to.* **Resources:** *Working Backwards* (Bryar & Carr) · Now/Next/Later (Bastow).

### Track 16 — Client/executive communication, strategic thinking & timezone
Executive-register communication (level up your ES/EN polish) · strategic framing/narrative · stakeholder management across EPAM + client leadership · **timezone:** Bogotá (UTC-5) is ~2–3h ahead of US Pacific — full West-Coast overlap is very achievable; present it as an advantage.

---

## Integrated schedule (~12–15 hrs/week)

Daily habit throughout: **LeetCode (medium-focus tiers), 30–45 min.** Java/Spring stays light; AI-SDLC and roadmapping/comms run as background threads.

| Week | Focus | LeetCode pattern |
|---|---|---|
| **1** | Big O mastery + Data Structures in Java (start) | Arrays & Hashing |
| **2** | Data Structures in Java (finish) + Java/Spring consolidation (light) | Two Pointers · Sliding Window |
| **3** | Microservices & integration design + AWS fundamentals (start) | Binary Search · Stack |
| **4** | AWS serverless/event-driven + AI-SDLC playbook framing + Bedrock/agentic | Linked List · Trees |
| **➡ BRANCH POINT (end of W4): confirm role/Camel scope, then pick A or B** | | |
| **5–7 (A)** | **Apache Camel deep** + K8s/AKS (CSI/PV/PVC) + observability + Terraform + performance | Graphs · Heap · Backtracking |
| **5–7 (B)** | **React (M1–M5) + FinDash** + keep AWS/microservices solid + Camel foundation only | Graphs · Heap · Backtracking |
| **8–10 (A)** | **Integration Showcase** capstone (Camel + Kafka/SQS + K8s + OTEL + Terraform + perf) | 1-D DP · Intervals · Greedy |
| **8–10 (B)** | React (M6–M7) + **FinDash full-stack** (wire to Spring Boot 4 + AWS backend) | 1-D DP · Intervals · Greedy |
| **11–12** | Capstone finalize + **AI-SDLC Playbook** + interview/design/roadmapping/exec-comms polish + mocks | 2-D DP/Tries/Math + high-freq hards; revisit weak patterns |

*Scaling:* full-time compresses to ~5–6 weeks; part-time stretches. The universal core (W1–4) is never wasted regardless of branch.

## Portfolio deliverables

1. **AI-SDLC Playbook** *(do regardless — your strongest universal piece):* documented SDD workflow + reusable AI skills/rules + an automation example + an adoption/change-management plan for a Rust/.NET→AWS/Java team + impact measurement. Optionally a small Bedrock AgentCore / Strands / Claude Agent SDK demo.
2. **Integration Showcase** *(Scenario A):* Camel 4.18 + Spring Boot 4 + Java 21 — large-file inbound → split/stream → transform → route to Kafka/SQS/SNS/EventBridge → DLQ/redelivery → K8s with PV/PVC + CSI → OpenTelemetry → Terraform → performance-tuned. Covers ~80% of the integration JD.
3. **FinDash full-stack** *(Scenario B — double duty):* React/Next.js frontend + **Spring Boot 4 + AWS backend** (REST, OAuth2/JWT, DynamoDB/Postgres, event-driven). Demonstrates React breadth **and** Java/Spring/AWS/microservices for the role.

## Interview-readiness checklist

- [ ] Big O of your own solutions on the spot; LeetCode mediums in ~30 min; pattern recognized in 2–3 min.
- [ ] Can design an integration/system on a whiteboard and walk through trade-offs.
- [ ] AWS serverless/event-driven patterns; K8s storage (CSI/PV/PVC); OTEL three pillars; Terraform basics.
- [ ] Camel mental model + EIP vocabulary (foundation), with a clear learning trajectory.
- [ ] The **AI-SDLC / SDD playbook** + a 2-minute adoption story; agentic-framework concepts.
- [ ] A one-page roadmap on demand; 4–6 STAR stories (roadmapping/scope · AI-champion · hard integration/perf · leadership).
- [ ] Timezone overlap framed as a strength.
- [ ] Portfolio live on GitHub: `dsa-java`, the **AI-SDLC Playbook**, and your Scenario capstone (Integration Showcase *or* FinDash full-stack).
