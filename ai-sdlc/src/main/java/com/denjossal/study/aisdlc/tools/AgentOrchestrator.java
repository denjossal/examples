package com.denjossal.study.aisdlc.tools;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Agent orchestration patterns for AI-assisted development.
 *
 * Demonstrates the core patterns used in agentic frameworks:
 * - Sequential: tasks executed one after another (pipeline)
 * - Parallel: independent tasks fan out and join
 * - Router: dynamic dispatch based on input classification
 * - Loop: iterative refinement until quality threshold met
 *
 * Maps to: Claude Agent SDK, LangGraph state machines, CrewAI role-based teams.
 */
public class AgentOrchestrator {

    public record AgentResult(String agentName, String output, boolean success) {}

    @FunctionalInterface
    public interface Agent {
        AgentResult execute(String input);
    }

    /**
     * Sequential pipeline: output of each agent feeds into the next.
     * Use when: tasks have strict dependencies (plan → implement → test).
     */
    public static List<AgentResult> sequential(String input, List<Agent> agents) {
        var results = new ArrayList<AgentResult>();
        String current = input;

        for (var agent : agents) {
            var result = agent.execute(current);
            results.add(result);
            if (!result.success()) break;
            current = result.output();
        }
        return results;
    }

    /**
     * Parallel fan-out: all agents process the same input concurrently.
     * Use when: tasks are independent (multi-reviewer, multi-search).
     */
    public static List<AgentResult> parallel(String input, List<Agent> agents) throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = agents.stream()
                    .map(agent -> executor.submit(() -> agent.execute(input)))
                    .toList();

            var results = new ArrayList<AgentResult>();
            for (var future : futures) {
                results.add(future.get());
            }
            return results;
        }
    }

    /**
     * Router: classifies input and dispatches to the appropriate agent.
     * Use when: different input types require different processing.
     */
    public static AgentResult route(String input, Function<String, String> classifier,
                                    Map<String, Agent> routes, Agent fallback) {
        String category = classifier.apply(input);
        var agent = routes.getOrDefault(category, fallback);
        return agent.execute(input);
    }

    /**
     * Loop with quality gate: iteratively refine until predicate passes or max iterations.
     * Use when: output quality is uncertain (code review → fix → re-review).
     */
    public static List<AgentResult> loopUntil(String input, Agent agent,
                                              Function<AgentResult, Boolean> qualityGate,
                                              int maxIterations) {
        var results = new ArrayList<AgentResult>();
        String current = input;

        for (int i = 0; i < maxIterations; i++) {
            var result = agent.execute(current);
            results.add(result);
            if (qualityGate.apply(result)) break;
            current = result.output();
        }
        return results;
    }
}
