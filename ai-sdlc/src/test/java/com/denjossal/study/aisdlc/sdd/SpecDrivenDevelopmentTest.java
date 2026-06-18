package com.denjossal.study.aisdlc.sdd;

import com.denjossal.study.aisdlc.sdd.SpecDrivenDevelopment.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SpecDrivenDevelopmentTest {

    private final SpecDrivenDevelopment sdd = new SpecDrivenDevelopment();

    @Test
    void shouldCreateSpec() {
        var spec = sdd.createSpec(
                "User Authentication",
                "Implement JWT-based auth for the REST API",
                List.of("Login returns JWT token", "Protected endpoints require valid token"),
                List.of("OAuth2 integration", "Password reset flow")
        );

        assertThat(spec.id()).isNotNull();
        assertThat(spec.title()).isEqualTo("User Authentication");
        assertThat(spec.acceptanceCriteria()).hasSize(2);
    }

    @Test
    void shouldTrackPhaseProgression() {
        var spec = sdd.createSpec("Feature", "desc", List.of("AC1"), List.of());
        assertThat(sdd.currentPhase(spec.id())).isEqualTo(Phase.SPEC);

        sdd.createPlan(spec.id(),
                List.of(new PlanStep(1, "Create user entity", List.of("User.java"), List.of())),
                List.of("Tight deadline"),
                "2 days"
        );
        assertThat(sdd.currentPhase(spec.id())).isEqualTo(Phase.PLAN);

        sdd.decomposePlanIntoTasks(spec.id(), "AI");
        assertThat(sdd.currentPhase(spec.id())).isEqualTo(Phase.TASKS);
    }

    @Test
    void shouldDecomposePlanIntoTasks() {
        var spec = sdd.createSpec("Feature", "desc", List.of("AC1"), List.of());
        sdd.createPlan(spec.id(),
                List.of(
                        new PlanStep(1, "Setup project", List.of("pom.xml"), List.of()),
                        new PlanStep(2, "Implement logic", List.of("Service.java"), List.of(1))
                ),
                List.of(), "1 day"
        );

        var tasks = sdd.decomposePlanIntoTasks(spec.id(), "Claude");

        assertThat(tasks).hasSize(2);
        assertThat(tasks.get(0).status()).isEqualTo(TaskStatus.PENDING);
        assertThat(tasks.get(0).assignee()).isEqualTo("Claude");
    }

    @Test
    void shouldThrowOnPlanWithoutSpec() {
        assertThatThrownBy(() -> sdd.createPlan("nonexistent", List.of(), List.of(), ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
