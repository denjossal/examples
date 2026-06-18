package com.denjossal.study.springboot.observability;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Distributed Tracing concepts — the three pillars of observability.
 *
 * Pillar 1: Metrics — aggregated numerical data (counters, gauges, histograms)
 * Pillar 2: Traces — request flow across services (spans with parent-child)
 * Pillar 3: Logs — discrete events with context (structured, correlated by traceId)
 *
 * OpenTelemetry concepts:
 * - Trace: end-to-end request journey (collection of spans)
 * - Span: one unit of work (service call, DB query, HTTP request)
 * - Context Propagation: traceId/spanId passed via headers (W3C traceparent)
 *
 * In production: use OTEL SDK auto-instrumentation + Collector + Dynatrace/Jaeger.
 */
public class DistributedTracing {

    public record Span(
            String traceId,
            String spanId,
            String parentSpanId,
            String operationName,
            String serviceName,
            Instant startTime,
            Instant endTime,
            Map<String, String> tags,
            SpanStatus status) {
        public long durationMs() {
            return endTime.toEpochMilli() - startTime.toEpochMilli();
        }
    }

    public enum SpanStatus {
        OK,
        ERROR
    }

    private final Map<String, List<Span>> traces = new ConcurrentHashMap<>();

    public SpanBuilder startSpan(String operationName, String serviceName) {
        return new SpanBuilder(this, operationName, serviceName, generateId(), null);
    }

    public SpanBuilder startChildSpan(String operationName, String serviceName, Span parent) {
        return new SpanBuilder(this, operationName, serviceName, parent.traceId(), parent.spanId());
    }

    public List<Span> getTrace(String traceId) {
        return traces.getOrDefault(traceId, List.of());
    }

    public Optional<Span> findSlowestSpan(String traceId) {
        return getTrace(traceId).stream().max(Comparator.comparingLong(Span::durationMs));
    }

    public List<Span> findErrors(String traceId) {
        return getTrace(traceId).stream()
                .filter(s -> s.status() == SpanStatus.ERROR)
                .toList();
    }

    void recordSpan(Span span) {
        traces.computeIfAbsent(span.traceId(), k -> new ArrayList<>()).add(span);
    }

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static class SpanBuilder {
        private final DistributedTracing tracer;
        private final String operationName;
        private final String serviceName;
        private final String traceId;
        private final String parentSpanId;
        private final Map<String, String> tags = new HashMap<>();
        private final Instant startTime = Instant.now();

        SpanBuilder(
                DistributedTracing tracer,
                String operationName,
                String serviceName,
                String traceId,
                String parentSpanId) {
            this.tracer = tracer;
            this.operationName = operationName;
            this.serviceName = serviceName;
            this.traceId = traceId;
            this.parentSpanId = parentSpanId;
        }

        public SpanBuilder tag(String key, String value) {
            tags.put(key, value);
            return this;
        }

        public Span end(SpanStatus status) {
            var span = new Span(
                    traceId,
                    UUID.randomUUID().toString().replace("-", "").substring(0, 16),
                    parentSpanId,
                    operationName,
                    serviceName,
                    startTime,
                    Instant.now(),
                    Map.copyOf(tags),
                    status);
            tracer.recordSpan(span);
            return span;
        }
    }
}
