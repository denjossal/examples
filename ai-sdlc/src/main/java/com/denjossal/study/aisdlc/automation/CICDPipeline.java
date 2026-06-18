package com.denjossal.study.aisdlc.automation;

import java.time.Instant;
import java.util.*;

/**
 * CI/CD pipeline model with AI integration points.
 *
 * Demonstrates where AI fits into a modern CI/CD pipeline:
 * - Pre-commit: AI code review, test generation
 * - PR stage: automated review comments, security scan
 * - Build stage: AI-assisted error diagnosis
 * - Deploy stage: AI-driven rollback decisions
 *
 * This is a simulation — in production, integrate with GitHub Actions/GitLab CI.
 */
public class CICDPipeline {

    public enum StageStatus { PENDING, RUNNING, PASSED, FAILED, SKIPPED }

    public record Stage(String name, StageStatus status, String output, Instant startedAt) {}
    public record PipelineRun(String id, String branch, List<Stage> stages, Instant triggeredAt) {}

    public record AIReviewFinding(String severity, String file, int line, String message, String suggestion) {}

    private final List<PipelineRun> history = new ArrayList<>();

    /**
     * Simulates a CI/CD pipeline run with AI-enhanced stages.
     */
    public PipelineRun run(String branch, String commitMessage, List<String> changedFiles) {
        var stages = new ArrayList<Stage>();
        var now = Instant.now();

        // Stage 1: Lint & Format
        stages.add(new Stage("lint", StageStatus.PASSED, "No issues", now));

        // Stage 2: AI Code Review (new — AI enhancement)
        var findings = aiCodeReview(changedFiles);
        var reviewStatus = findings.stream().anyMatch(f -> f.severity().equals("critical"))
                ? StageStatus.FAILED : StageStatus.PASSED;
        stages.add(new Stage("ai-review", reviewStatus,
                "%d findings (%d critical)".formatted(
                        findings.size(),
                        findings.stream().filter(f -> f.severity().equals("critical")).count()
                ), now));

        // Stage 3: Build
        if (reviewStatus == StageStatus.FAILED) {
            stages.add(new Stage("build", StageStatus.SKIPPED, "Skipped due to critical findings", now));
            stages.add(new Stage("test", StageStatus.SKIPPED, "Skipped", now));
            stages.add(new Stage("deploy", StageStatus.SKIPPED, "Skipped", now));
        } else {
            stages.add(new Stage("build", StageStatus.PASSED, "Build successful", now));
            stages.add(new Stage("test", StageStatus.PASSED, "42 tests passed", now));

            // Stage 5: Deploy (only for main branch)
            if (branch.equals("main")) {
                stages.add(new Stage("deploy", StageStatus.PASSED, "Deployed to production", now));
            } else {
                stages.add(new Stage("deploy", StageStatus.SKIPPED, "Not main branch", now));
            }
        }

        var run = new PipelineRun(UUID.randomUUID().toString(), branch, stages, now);
        history.add(run);
        return run;
    }

    /**
     * Simulates AI-powered code review that catches common issues.
     */
    public List<AIReviewFinding> aiCodeReview(List<String> changedFiles) {
        var findings = new ArrayList<AIReviewFinding>();

        for (var file : changedFiles) {
            if (file.contains("password") || file.contains("secret")) {
                findings.add(new AIReviewFinding(
                        "critical", file, 1,
                        "Potential secret in filename",
                        "Use environment variables or a secrets manager"
                ));
            }
            if (file.endsWith("Controller.java")) {
                findings.add(new AIReviewFinding(
                        "medium", file, 0,
                        "Controller changed — verify input validation",
                        "Ensure @Valid annotations on request bodies"
                ));
            }
        }
        return findings;
    }

    /**
     * AI-assisted failure diagnosis: suggests probable root cause.
     */
    public String diagnoseFailure(Stage failedStage) {
        return switch (failedStage.name()) {
            case "build" -> "Probable cause: compilation error. Check recent dependency changes.";
            case "test" -> "Probable cause: test regression. Run failed tests locally with -X flag.";
            case "deploy" -> "Probable cause: infrastructure issue. Check service health endpoints.";
            case "ai-review" -> "Critical findings detected. Review AI suggestions before proceeding.";
            default -> "Unknown failure in stage: " + failedStage.name();
        };
    }

    public List<PipelineRun> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
