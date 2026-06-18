package com.denjossal.study.aisdlc.sdd;

import java.time.Instant;
import java.util.*;

/**
 * Spec-Driven Development (SDD) — methodology for AI-assisted software development.
 *
 * Flow: Spec → Plan → Tasks → Implementation (with review gates)
 *
 * Key principles:
 * 1. Start with a spec (what, why, acceptance criteria) — never jump to code
 * 2. Plan decomposes the spec into ordered tasks with dependencies
 * 3. Each task is small enough for one AI context window
 * 4. Review gates between phases catch drift early
 * 5. Handoff conventions ensure continuity across sessions
 */
public class SpecDrivenDevelopment {

    public enum Phase {
        SPEC,
        PLAN,
        TASKS,
        IMPLEMENTATION,
        REVIEW
    }

    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        BLOCKED
    }

    public record Spec(
            String id,
            String title,
            String description,
            List<String> acceptanceCriteria,
            List<String> outOfScope,
            Instant createdAt) {}

    public record Plan(String specId, List<PlanStep> steps, List<String> risks, String estimatedEffort) {}

    public record PlanStep(int order, String description, List<String> files, List<Integer> dependsOn) {}

    public record Task(
            String id, String planStepDescription, TaskStatus status, String assignee, List<String> subtasks) {}

    private final Map<String, Spec> specs = new LinkedHashMap<>();
    private final Map<String, Plan> plans = new LinkedHashMap<>();
    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();

    public Spec createSpec(String title, String description, List<String> acceptanceCriteria, List<String> outOfScope) {
        var spec = new Spec(
                UUID.randomUUID().toString(), title, description, acceptanceCriteria, outOfScope, Instant.now());
        specs.put(spec.id(), spec);
        return spec;
    }

    public Plan createPlan(String specId, List<PlanStep> steps, List<String> risks, String estimatedEffort) {
        if (!specs.containsKey(specId)) {
            throw new IllegalArgumentException("Spec not found: " + specId);
        }
        var plan = new Plan(specId, steps, risks, estimatedEffort);
        plans.put(specId, plan);
        return plan;
    }

    public List<Task> decomposePlanIntoTasks(String specId, String assignee) {
        var plan = plans.get(specId);
        if (plan == null) throw new IllegalArgumentException("Plan not found for spec: " + specId);

        var taskList = new ArrayList<Task>();
        for (var step : plan.steps()) {
            taskList.add(new Task(
                    UUID.randomUUID().toString(), step.description(), TaskStatus.PENDING, assignee, List.of()));
        }
        tasks.put(specId, taskList);
        return taskList;
    }

    public Phase currentPhase(String specId) {
        if (!specs.containsKey(specId)) return null;
        if (!plans.containsKey(specId)) return Phase.SPEC;
        if (!tasks.containsKey(specId)) return Phase.PLAN;

        var taskList = tasks.get(specId);
        boolean allCompleted = taskList.stream().allMatch(t -> t.status() == TaskStatus.COMPLETED);
        if (allCompleted) return Phase.REVIEW;

        boolean anyInProgress = taskList.stream().anyMatch(t -> t.status() == TaskStatus.IN_PROGRESS);
        if (anyInProgress) return Phase.IMPLEMENTATION;

        return Phase.TASKS;
    }

    public Optional<Spec> getSpec(String specId) {
        return Optional.ofNullable(specs.get(specId));
    }

    public Optional<Plan> getPlan(String specId) {
        return Optional.ofNullable(plans.get(specId));
    }

    public List<Task> getTasks(String specId) {
        return tasks.getOrDefault(specId, List.of());
    }
}
