package com.denjossal.study.aisdlc.automation;

import com.denjossal.study.aisdlc.automation.CICDPipeline.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CICDPipelineTest {

    private final CICDPipeline pipeline = new CICDPipeline();

    @Test
    void shouldRunSuccessfulPipeline() {
        var run = pipeline.run("main", "add feature", List.of("Service.java"));

        assertThat(run.stages()).hasSize(5);
        assertThat(run.stages().get(4).name()).isEqualTo("deploy");
        assertThat(run.stages().get(4).status()).isEqualTo(StageStatus.PASSED);
    }

    @Test
    void shouldSkipDeployOnNonMainBranch() {
        var run = pipeline.run("feature/auth", "wip", List.of("Auth.java"));

        var deploy = run.stages().stream().filter(s -> s.name().equals("deploy")).findFirst();
        assertThat(deploy).isPresent();
        assertThat(deploy.get().status()).isEqualTo(StageStatus.SKIPPED);
    }

    @Test
    void shouldFailOnCriticalSecurityFinding() {
        var run = pipeline.run("main", "add config", List.of("password.txt"));

        var review = run.stages().stream().filter(s -> s.name().equals("ai-review")).findFirst();
        assertThat(review).isPresent();
        assertThat(review.get().status()).isEqualTo(StageStatus.FAILED);

        var build = run.stages().stream().filter(s -> s.name().equals("build")).findFirst();
        assertThat(build.get().status()).isEqualTo(StageStatus.SKIPPED);
    }

    @Test
    void shouldDetectControllerChanges() {
        var findings = pipeline.aiCodeReview(List.of("UserController.java", "Service.java"));

        assertThat(findings).hasSize(1);
        assertThat(findings.get(0).severity()).isEqualTo("medium");
        assertThat(findings.get(0).message()).contains("input validation");
    }

    @Test
    void shouldDiagnoseFailure() {
        var stage = new Stage("test", StageStatus.FAILED, "3 tests failed", null);
        var diagnosis = pipeline.diagnoseFailure(stage);

        assertThat(diagnosis).contains("test regression");
    }

    @Test
    void shouldTrackHistory() {
        pipeline.run("main", "commit 1", List.of("A.java"));
        pipeline.run("main", "commit 2", List.of("B.java"));

        assertThat(pipeline.getHistory()).hasSize(2);
    }
}
