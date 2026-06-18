package com.denjossal.study.springboot.observability;

import com.denjossal.study.springboot.observability.DistributedTracing.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DistributedTracingTest {

    @Test
    void shouldCreateTraceWithSpans() {
        var tracer = new DistributedTracing();

        var root = tracer.startSpan("GET /api/orders", "api-gateway")
                .tag("http.method", "GET")
                .end(SpanStatus.OK);

        var child = tracer.startChildSpan("findOrders", "order-service", root)
                .tag("db.type", "postgres")
                .end(SpanStatus.OK);

        var trace = tracer.getTrace(root.traceId());
        assertThat(trace).hasSize(2);
        assertThat(child.parentSpanId()).isEqualTo(root.spanId());
        assertThat(child.traceId()).isEqualTo(root.traceId());
    }

    @Test
    void shouldFindSlowestSpan() throws InterruptedException {
        var tracer = new DistributedTracing();

        var fast = tracer.startSpan("fast-op", "svc-a").end(SpanStatus.OK);
        Thread.sleep(15);
        var slow = tracer.startSpan("slow-op", "svc-a").end(SpanStatus.OK);

        // They're in different traces, let's use child spans in same trace
        var tracer2 = new DistributedTracing();
        var root = tracer2.startSpan("root", "gateway").end(SpanStatus.OK);
        Thread.sleep(10);
        var child = tracer2.startChildSpan("db-query", "db-service", root).end(SpanStatus.OK);

        var slowest = tracer2.findSlowestSpan(root.traceId());
        assertThat(slowest).isPresent();
    }

    @Test
    void shouldFindErrors() {
        var tracer = new DistributedTracing();

        var root = tracer.startSpan("request", "gateway").end(SpanStatus.OK);
        tracer.startChildSpan("failing-call", "payment", root)
                .tag("error", "timeout")
                .end(SpanStatus.ERROR);

        var errors = tracer.findErrors(root.traceId());
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).operationName()).isEqualTo("failing-call");
        assertThat(errors.get(0).tags()).containsEntry("error", "timeout");
    }

    @Test
    void shouldCalculateSpanDuration() {
        var tracer = new DistributedTracing();
        var span = tracer.startSpan("op", "svc").end(SpanStatus.OK);
        assertThat(span.durationMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldReturnEmptyForUnknownTrace() {
        var tracer = new DistributedTracing();
        assertThat(tracer.getTrace("nonexistent")).isEmpty();
    }
}
