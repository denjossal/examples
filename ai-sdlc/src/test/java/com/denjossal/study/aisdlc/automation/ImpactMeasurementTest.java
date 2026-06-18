package com.denjossal.study.aisdlc.automation;

import static org.assertj.core.api.Assertions.*;

import com.denjossal.study.aisdlc.automation.ImpactMeasurement.*;
import org.junit.jupiter.api.Test;

class ImpactMeasurementTest {

    @Test
    void shouldCalculateImprovement() {
        var metric = new Metric("Cycle time", 48, 12, " hrs");
        assertThat(metric.improvement()).isCloseTo(-75.0, within(0.1));
    }

    @Test
    void shouldGenerateSummary() {
        var metric = new Metric("Test coverage", 62, 84, "%");
        assertThat(metric.summary()).contains("62").contains("84").contains("improvement");
    }

    @Test
    void shouldHandleZeroBaseline() {
        var metric = new Metric("Something", 0, 5, "x");
        assertThat(metric.improvement()).isEqualTo(0);
    }

    @Test
    void shouldCreateSampleReport() {
        var report = ImpactMeasurement.sampleReport();

        assertThat(report.metrics()).hasSize(6);
        assertThat(report.qualitativeFindings()).isNotEmpty();
        assertThat(report.teamName()).isEqualTo("Platform Team");
    }

    @Test
    void shouldComputeAverageImprovement() {
        var report = ImpactMeasurement.sampleReport();
        assertThat(report.averageImprovement()).isNotEqualTo(0);
    }

    @Test
    void shouldGenerateExecutiveSummary() {
        var report = ImpactMeasurement.sampleReport();
        var summary = ImpactMeasurement.executiveSummary(report);

        assertThat(summary).contains("# AI-SDLC Impact Report");
        assertThat(summary).contains("Key Metrics");
        assertThat(summary).contains("Average Improvement");
        assertThat(summary).contains("Qualitative Findings");
    }
}
