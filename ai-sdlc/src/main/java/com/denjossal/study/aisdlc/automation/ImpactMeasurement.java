package com.denjossal.study.aisdlc.automation;

import java.util.*;

/**
 * AI-SDLC impact measurement — quantify the value of AI in development.
 *
 * Key metrics for demonstrating AI adoption value:
 * - Velocity: tasks completed per sprint, cycle time reduction
 * - Quality: defect rate, code review findings caught pre-merge
 * - Developer experience: time saved on boilerplate, context switching reduction
 * - Coverage: test coverage delta, documentation coverage
 */
public class ImpactMeasurement {

    public record Metric(String name, double baseline, double withAI, String unit) {
        public double improvement() {
            if (baseline == 0) return 0;
            return ((withAI - baseline) / Math.abs(baseline)) * 100;
        }

        public String summary() {
            double pct = improvement();
            String direction = pct > 0 ? "improvement" : "regression";
            return "%s: %.1f%s → %.1f%s (%.1f%% %s)"
                    .formatted(name, baseline, unit, withAI, unit, Math.abs(pct), direction);
        }
    }

    public record ImpactReport(String teamName, String period, List<Metric> metrics, List<String> qualitativeFindings) {
        public double averageImprovement() {
            return metrics.stream().mapToDouble(Metric::improvement).average().orElse(0);
        }
    }

    /**
     * Example metrics from an AI-SDLC adoption (representative of real-world data).
     */
    public static ImpactReport sampleReport() {
        var metrics = List.of(
                new Metric("PR cycle time", 48, 12, " hrs"),
                new Metric("Defects escaped to prod", 8, 3, "/sprint"),
                new Metric("Test coverage", 62, 84, "%"),
                new Metric("Code review turnaround", 24, 4, " hrs"),
                new Metric("Boilerplate time", 6, 1.5, " hrs/week"),
                new Metric("Documentation coverage", 40, 78, "%"));

        var qualitative = List.of(
                "Junior developers onboard 40% faster with AI-generated explanations",
                "Consistent code style without manual enforcement — AI applies conventions",
                "Architecture decisions documented in real-time via AI summaries",
                "Reduced context-switching: AI handles repetitive tasks in-flow");

        return new ImpactReport("Platform Team", "Q1 2026", metrics, qualitative);
    }

    /**
     * Generates an executive summary suitable for stakeholder communication.
     */
    public static String executiveSummary(ImpactReport report) {
        var sb = new StringBuilder();
        sb.append("# AI-SDLC Impact Report — %s (%s)\n\n".formatted(report.teamName(), report.period()));
        sb.append("## Key Metrics\n\n");

        for (var metric : report.metrics()) {
            sb.append("- ").append(metric.summary()).append("\n");
        }

        sb.append("\n## Average Improvement: %.1f%%\n".formatted(report.averageImprovement()));
        sb.append("\n## Qualitative Findings\n\n");

        for (var finding : report.qualitativeFindings()) {
            sb.append("- ").append(finding).append("\n");
        }
        return sb.toString();
    }
}
