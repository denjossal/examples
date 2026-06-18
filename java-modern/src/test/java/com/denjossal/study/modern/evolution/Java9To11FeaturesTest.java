package com.denjossal.study.modern.evolution;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

class Java9To11FeaturesTest {

    @Test
    void shouldCreateImmutableCollections() {
        assertThat(Java9To11Features.immutableList()).containsExactly("one", "two", "three");
        assertThat(Java9To11Features.immutableMap()).containsEntry("a", 1);
    }

    @Test
    void shouldTakeWhile() {
        var result = Java9To11Features.takeWhileLessThan(List.of(1, 2, 3, 5, 8, 2), 5);
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    void shouldDropWhile() {
        var result = Java9To11Features.dropWhileLessThan(List.of(1, 2, 3, 5, 8, 2), 5);
        assertThat(result).containsExactly(5, 8, 2);
    }

    @Test
    void shouldIterateWithPredicate() {
        var result = Java9To11Features.iterateWithPredicate(0, 10).toList();
        assertThat(result).containsExactly(0, 2, 4, 6, 8);
    }

    @Test
    void shouldHandleOptionalOr() {
        var present = Java9To11Features.optionalOr(Optional.of("found"), "default");
        assertThat(present).contains("found");

        var absent = Java9To11Features.optionalOr(Optional.empty(), "default");
        assertThat(absent).contains("default");
    }

    @Test
    void shouldHandleIfPresentOrElse() {
        assertThat(Java9To11Features.optionalOrAction(Optional.of("hi"))).isEqualTo("Found: hi");
        assertThat(Java9To11Features.optionalOrAction(Optional.empty())).isEqualTo("Not found");
    }

    @Test
    void shouldProcessWithVar() {
        var result = Java9To11Features.processWithVar(List.of("hello", "  world  ", "", "  "));
        assertThat(result).containsExactly("hello", "world");
    }

    @Test
    void shouldCheckBlank() {
        assertThat(Java9To11Features.isBlankOrEmpty("   ")).isTrue();
        assertThat(Java9To11Features.isBlankOrEmpty("hi")).isFalse();
    }

    @Test
    void shouldSplitLines() {
        assertThat(Java9To11Features.splitLines("a\nb\nc")).containsExactly("a", "b", "c");
    }

    @Test
    void shouldRepeatAndStrip() {
        assertThat(Java9To11Features.repeatAndStrip("ha", 3)).isEqualTo("hahaha");
    }
}
