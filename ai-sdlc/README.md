# ai-sdlc

An AI-assisted SDLC playbook, expressed as small Java models — a methodology demonstration of where AI fits across spec, build, and measurement.

## Packages

### `sdd` — Spec-Driven Development
- **`SpecDrivenDevelopment`** — models the `Spec → Plan → Tasks → Implementation → Review` flow with review gates. Holds `Spec` (title, acceptance criteria, out-of-scope), `Plan` (ordered `PlanStep`s with dependencies + risks), and `Task` records; `currentPhase()` derives the active phase from task state. Principle: never jump to code, keep each task within one AI context window.

### `tools` — prompting + orchestration
- **`PromptTemplate`** — `{{variable}}` substitution engine with missing-variable validation, plus typed `Message`/`Role` builders for code-review and test-generation prompts (system + few-shot + user, with output-format constraints).
- **`AgentOrchestrator`** — the four core agentic patterns over a functional `Agent` interface: `sequential` (pipeline), `parallel` (virtual-thread fan-out/join), `route` (classifier-based dispatch), and `loopUntil` (refine until a quality gate passes). Maps to Claude Agent SDK / LangGraph / CrewAI.

### `automation` — pipeline + impact
- **`CICDPipeline`** — simulates a CI/CD run with AI-enhanced stages (lint → AI review → build → test → deploy). `aiCodeReview` flags secrets/controller risks; critical findings fail the run; `diagnoseFailure` suggests probable root cause per stage.
- **`ImpactMeasurement`** — quantifies AI adoption value: `Metric` (baseline vs withAI + `improvement()` %), `ImpactReport` with averages and qualitative findings, and an `executiveSummary()` markdown generator for stakeholders.

### `resources/playbook`
Placeholder directory reserved for narrative playbook content (templates, conventions) accompanying the code models above.

## Notes

These are in-memory models and simulations — no live AI APIs or external services are called. The module illustrates the *methodology* and the shapes of the data, not a production integration. Standard build (JDK 25, Spotless-formatted).
