package com.denjossal.study.modern.evolution;

import static org.assertj.core.api.Assertions.*;

import com.denjossal.study.modern.evolution.Java17To21Features.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class Java17To21FeaturesTest {

    @Test
    void shouldDescribeSealedResult() {
        assertThat(Java17To21Features.describeResult(new Success<>("data"))).isEqualTo("Success: data");
        assertThat(Java17To21Features.describeResult(new Failure<>("timeout", null)))
                .isEqualTo("Failed: timeout");
        assertThat(Java17To21Features.describeResult(new Pending<>("task-123"))).isEqualTo("Pending: task-123");
    }

    @Test
    void shouldClassifyNumbers() {
        assertThat(Java17To21Features.classifyNumber(-5)).isEqualTo("negative");
        assertThat(Java17To21Features.classifyNumber(0)).isEqualTo("zero");
        assertThat(Java17To21Features.classifyNumber(42)).isEqualTo("positive: 42");
        assertThat(Java17To21Features.classifyNumber(Double.NaN)).isEqualTo("NaN");
        assertThat(Java17To21Features.classifyNumber(3.14)).isEqualTo("double: 3.14");
        assertThat(Java17To21Features.classifyNumber("hi")).isEqualTo("string: hi");
        assertThat(Java17To21Features.classifyNumber(null)).isEqualTo("null");
    }

    @Test
    void shouldComputeAreaWithRecordPatterns() {
        assertThat(Java17To21Features.area(new Circle(5.0))).isCloseTo(78.539, within(0.01));
        assertThat(Java17To21Features.area(new Rect(3.0, 4.0))).isEqualTo(12.0);
        assertThat(Java17To21Features.area(new Triangle(6.0, 3.0))).isEqualTo(9.0);
    }

    @Test
    void shouldDeconstructNestedRecordPatterns() {
        var line = new Line(new Point(0, 0), new Point(3, 4));
        assertThat(Java17To21Features.lineLength(line)).isEqualTo(5.0);
        assertThat(Java17To21Features.lineLength("not a line")).isEqualTo(0.0);
    }

    @Test
    void shouldFetchWithVirtualThreads() throws Exception {
        var urls = List.of("url1", "url2", "url3");
        var results = Java17To21Features.fetchAllWithVirtualThreads(urls);

        assertThat(results).hasSize(3);
        assertThat(results.get(0)).isEqualTo("Response from url1");
    }

    @Test
    void shouldUseSequencedCollections() {
        var list = new ArrayList<>(List.of(1, 2, 3, 4, 5));

        assertThat(Java17To21Features.getFirst(list)).isEqualTo(1);
        assertThat(Java17To21Features.getLast(list)).isEqualTo(5);

        var reversed = Java17To21Features.reversed(list);
        assertThat(reversed.getFirst()).isEqualTo(5);
    }
}
