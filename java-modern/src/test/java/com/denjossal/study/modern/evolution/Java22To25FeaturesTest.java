package com.denjossal.study.modern.evolution;

import com.denjossal.study.modern.evolution.Java22To25Features.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class Java22To25FeaturesTest {

    @Test
    void shouldCountWithUnnamedVariable() {
        assertThat(Java22To25Features.countElements(List.of("a", "b", "c"))).isEqualTo(3);
    }

    @Test
    void shouldSafeParseIntegers() {
        assertThat(Java22To25Features.safeParse("42")).contains(42);
        assertThat(Java22To25Features.safeParse("abc")).isEmpty();
    }

    @Test
    void shouldWindowFixed() {
        var result = Java22To25Features.windowedChunks(List.of(1, 2, 3, 4, 5, 6), 3);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).containsExactly(1, 2, 3);
        assertThat(result.get(1)).containsExactly(4, 5, 6);
    }

    @Test
    void shouldSlidingWindow() {
        var result = Java22To25Features.slidingWindows(List.of(1, 2, 3, 4, 5), 3);
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsExactly(1, 2, 3);
        assertThat(result.get(1)).containsExactly(2, 3, 4);
        assertThat(result.get(2)).containsExactly(3, 4, 5);
    }

    @Test
    void shouldComputePrefixSums() {
        var result = Java22To25Features.prefixSums(List.of(1, 2, 3, 4, 5));
        assertThat(result).containsExactly(1, 3, 6, 10, 15);
    }

    @Test
    void shouldFetchConcurrentlyWithGatherer() {
        var urls = List.of("a.com", "b.com", "c.com");
        var results = Java22To25Features.fetchConcurrently(urls, 3);

        assertThat(results).hasSize(3);
        assertThat(results).allMatch(r -> r.startsWith("Response: "));
    }

    @Test
    void shouldUseStructuredConcurrency() throws Exception {
        var profile = Java22To25Features.fetchProfile(7);

        assertThat(profile.name()).isEqualTo("User-7");
        assertThat(profile.age()).isEqualTo(32);
        assertThat(profile.email()).isEqualTo("user7@example.com");
    }

    @Test
    void shouldCreateValidatedList() {
        var list = new ValidatedList<>(List.of(1, 2, 3));
        assertThat(list).containsExactly(1, 2, 3);

        assertThatThrownBy(() -> new ValidatedList<>(null))
                .isInstanceOf(NullPointerException.class);
    }
}
