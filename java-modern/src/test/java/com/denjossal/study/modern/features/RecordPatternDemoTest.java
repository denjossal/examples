package com.denjossal.study.modern.features;

import static org.assertj.core.api.Assertions.*;

import com.denjossal.study.modern.features.RecordPatternDemo.*;
import org.junit.jupiter.api.Test;

class RecordPatternDemoTest {

    @Test
    void shouldEvaluateSimpleExpression() {
        Expr expr = new Add(new Num(3), new Num(4));
        assertThat(RecordPatternDemo.evaluate(expr)).isEqualTo(7);
    }

    @Test
    void shouldEvaluateNestedExpression() {
        // (2 + 3) * 4 = 20
        Expr expr = new Mul(new Add(new Num(2), new Num(3)), new Num(4));
        assertThat(RecordPatternDemo.evaluate(expr)).isEqualTo(20);
    }

    @Test
    void shouldFormatExpression() {
        Expr expr = new Add(new Mul(new Num(2), new Num(3)), new Num(1));
        assertThat(RecordPatternDemo.format(expr)).isEqualTo("((2 * 3) + 1)");
    }

    @Test
    void shouldComputeLineLength() {
        var line = new Line(new Point(0, 0), new Point(3, 4));
        assertThat(RecordPatternDemo.length(line)).isEqualTo(5.0);
    }

    @Test
    void shouldHandleZeroLengthLine() {
        var line = new Line(new Point(1, 1), new Point(1, 1));
        assertThat(RecordPatternDemo.length(line)).isEqualTo(0.0);
    }
}
