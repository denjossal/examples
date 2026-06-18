package com.denjossal.study.aisdlc.tools;

import com.denjossal.study.aisdlc.tools.AgentOrchestrator.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class AgentOrchestratorTest {

    private Agent echoAgent(String name) {
        return input -> new AgentResult(name, name + ": " + input, true);
    }

    private Agent failAgent(String name) {
        return input -> new AgentResult(name, "failed", false);
    }

    @Test
    void shouldRunSequentialPipeline() {
        var results = AgentOrchestrator.sequential("start",
                List.of(echoAgent("planner"), echoAgent("coder"), echoAgent("reviewer"))
        );

        assertThat(results).hasSize(3);
        assertThat(results.get(0).output()).isEqualTo("planner: start");
        assertThat(results.get(1).output()).isEqualTo("coder: planner: start");
        assertThat(results.get(2).output()).isEqualTo("reviewer: coder: planner: start");
    }

    @Test
    void shouldStopSequentialOnFailure() {
        var results = AgentOrchestrator.sequential("input",
                List.of(echoAgent("first"), failAgent("broken"), echoAgent("unreached"))
        );

        assertThat(results).hasSize(2);
        assertThat(results.get(1).success()).isFalse();
    }

    @Test
    void shouldRunParallel() throws Exception {
        var results = AgentOrchestrator.parallel("query",
                List.of(echoAgent("search1"), echoAgent("search2"), echoAgent("search3"))
        );

        assertThat(results).hasSize(3);
        assertThat(results).allMatch(AgentResult::success);
    }

    @Test
    void shouldRouteToCorrectAgent() {
        var routes = Map.<String, Agent>of(
                "bug", echoAgent("debugger"),
                "feature", echoAgent("builder")
        );

        var result = AgentOrchestrator.route("fix the crash",
                input -> input.contains("fix") ? "bug" : "feature",
                routes,
                echoAgent("default")
        );

        assertThat(result.agentName()).isEqualTo("debugger");
    }

    @Test
    void shouldLoopUntilQualityMet() {
        var counter = new int[]{0};
        Agent refiner = input -> {
            counter[0]++;
            return new AgentResult("refiner", "v" + counter[0], counter[0] >= 3);
        };

        var results = AgentOrchestrator.loopUntil("draft",
                refiner,
                AgentResult::success,
                5
        );

        assertThat(results).hasSize(3);
        assertThat(results.getLast().output()).isEqualTo("v3");
    }

    @Test
    void shouldStopAtMaxIterations() {
        Agent neverDone = input -> new AgentResult("agent", "still going", false);

        var results = AgentOrchestrator.loopUntil("start", neverDone, AgentResult::success, 3);

        assertThat(results).hasSize(3);
    }
}
