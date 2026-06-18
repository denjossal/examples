package com.denjossal.study.aws.lambda;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class LambdaColdStartOptimizationTest {

    @Test
    void shouldInitializeOnConstruction() {
        var lambda = new LambdaColdStartOptimization();
        assertThat(lambda.getInitTimeMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldHandleGreetAction() {
        var lambda = new LambdaColdStartOptimization();
        var result = lambda.handleRequest(Map.of("action", "greet"));

        assertThat(result.get("statusCode")).isEqualTo(200);
        assertThat(result.get("body").toString()).contains("Hello from Lambda");
    }

    @Test
    void shouldHandleConfigAction() {
        var lambda = new LambdaColdStartOptimization();
        var result = lambda.handleRequest(Map.of("action", "config"));

        assertThat(result.get("body").toString()).contains("DB_URL");
    }

    @Test
    void shouldHandleDefaultAction() {
        var lambda = new LambdaColdStartOptimization();
        var result = lambda.handleRequest(Map.of());

        assertThat(result.get("body").toString()).contains("Unknown action: default");
    }

    @Test
    void shouldIncludeMetrics() {
        var lambda = new LambdaColdStartOptimization();
        var result = lambda.handleRequest(Map.of("action", "greet"));

        @SuppressWarnings("unchecked")
        var metrics = (Map<String, Object>) result.get("metrics");
        assertThat(metrics).containsKeys("initTimeMs", "handlerTimeMs", "cachedConfigSize");
    }

    @Test
    void shouldReuseAcrossInvocations() {
        var lambda = new LambdaColdStartOptimization();

        // First invocation (warm)
        lambda.handleRequest(Map.of("action", "greet"));
        // Second invocation — init time is the same (not re-initialized)
        var result = lambda.handleRequest(Map.of("action", "greet"));

        @SuppressWarnings("unchecked")
        var metrics = (Map<String, Object>) result.get("metrics");
        assertThat((long) metrics.get("initTimeMs")).isEqualTo(lambda.getInitTimeMs());
    }
}
