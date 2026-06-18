package com.denjossal.study.modern.evolution;

import static org.assertj.core.api.Assertions.*;

import com.denjossal.study.modern.evolution.Java12To16Features.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class Java12To16FeaturesTest {

    @Test
    void shouldClassifyDays() {
        assertThat(Java12To16Features.dayType("Monday")).isEqualTo("Weekday");
        assertThat(Java12To16Features.dayType("Sunday")).isEqualTo("Weekend");
        assertThat(Java12To16Features.dayType("Holiday")).isEqualTo("Unknown");
    }

    @Test
    void shouldReturnNumericDayValue() {
        assertThat(Java12To16Features.numericDayValue("Monday")).isEqualTo(1);
        assertThat(Java12To16Features.numericDayValue("Friday")).isEqualTo(5);
    }

    @Test
    void shouldGenerateJsonTextBlock() {
        var json = Java12To16Features.jsonTemplate("Alice", 30);
        assertThat(json).contains("\"name\": \"Alice\"");
        assertThat(json).contains("\"age\": 30");
    }

    @Test
    void shouldGenerateSqlTextBlock() {
        var sql = Java12To16Features.sqlQuery();
        assertThat(sql).contains("SELECT u.name");
        assertThat(sql).contains("WHERE o.total > 100");
    }

    @Test
    void shouldCreateAndUseRecords() {
        var money = new Money(100.0, "usd");
        assertThat(money.currency()).isEqualTo("USD"); // compact constructor uppercases
        assertThat(money.amount()).isEqualTo(100.0);

        var sum = money.add(new Money(50.0, "USD"));
        assertThat(sum.amount()).isEqualTo(150.0);
    }

    @Test
    void shouldValidateRecordConstruction() {
        assertThatThrownBy(() -> new Money(-1, "USD")).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Range(10, 5)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldUseRangeRecord() {
        var range = new Range(1, 10);
        assertThat(range.contains(5)).isTrue();
        assertThat(range.contains(11)).isFalse();
        assertThat(range.length()).isEqualTo(9);
    }

    @Test
    void shouldPatternMatchInstanceof() {
        assertThat(Java12To16Features.describeNewWay("hello")).isEqualTo("String of length 5");
        assertThat(Java12To16Features.describeNewWay(42)).isEqualTo("Positive integer: 42");
        assertThat(Java12To16Features.describeNewWay(List.of(1, 2))).isEqualTo("Non-empty list of size 2");
    }

    @Test
    void shouldFilterAndCollect() {
        var result = Java12To16Features.filterAndCollect(List.of("apple", "avocado", "banana", "apricot"), "a");
        assertThat(result).containsExactly("apple", "avocado", "apricot");
    }

    @Test
    void shouldComputeStats() {
        var stats = Java12To16Features.computeStats(List.of(10, 20, 30));
        assertThat(stats.count()).isEqualTo(3);
        assertThat(stats.average()).isEqualTo(20.0);
    }
}
