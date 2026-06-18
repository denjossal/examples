package com.denjossal.study.modern.features;

import static org.assertj.core.api.Assertions.*;

import com.denjossal.study.modern.features.SealedInterfaceDemo.*;
import org.junit.jupiter.api.Test;

class SealedInterfaceDemoTest {

    @Test
    void shouldComputeCircleArea() {
        assertThat(SealedInterfaceDemo.area(new Circle(5.0))).isCloseTo(78.539, within(0.01));
    }

    @Test
    void shouldComputeRectangleArea() {
        assertThat(SealedInterfaceDemo.area(new Rectangle(3.0, 4.0))).isEqualTo(12.0);
    }

    @Test
    void shouldComputeTriangleArea() {
        assertThat(SealedInterfaceDemo.area(new Triangle(6.0, 3.0))).isEqualTo(9.0);
    }

    @Test
    void shouldDescribeLargeCircle() {
        assertThat(SealedInterfaceDemo.describe(new Circle(150))).isEqualTo("Large circle");
    }

    @Test
    void shouldDescribeSquare() {
        assertThat(SealedInterfaceDemo.describe(new Rectangle(5.0, 5.0))).isEqualTo("Square 5.0x5.0");
    }

    @Test
    void shouldDescribeRegularCircle() {
        assertThat(SealedInterfaceDemo.describe(new Circle(3.0))).isEqualTo("Circle with radius 3.0");
    }
}
