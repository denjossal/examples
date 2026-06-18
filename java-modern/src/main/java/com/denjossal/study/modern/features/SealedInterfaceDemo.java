package com.denjossal.study.modern.features;

/**
 * Demonstrates sealed interfaces (Java 17) + pattern matching for switch (Java 21).
 * Sealed types restrict the hierarchy — the compiler knows all subtypes,
 * enabling exhaustive pattern matching without a default branch.
 */
public class SealedInterfaceDemo {

    public sealed interface Shape permits Circle, Rectangle, Triangle {}

    public record Circle(double radius) implements Shape {}
    public record Rectangle(double width, double height) implements Shape {}
    public record Triangle(double base, double height) implements Shape {}

    /**
     * Pattern matching with exhaustive switch (Java 21).
     * No default needed because Shape is sealed and all permits are covered.
     */
    public static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Triangle t -> 0.5 * t.base() * t.height();
        };
    }

    /**
     * Pattern matching with instanceof + sealed knowledge.
     * Guarded patterns (when clause) require Java 21; this version uses if-chains.
     */
    public static String describe(Shape shape) {
        if (shape instanceof Circle c) {
            return c.radius() > 100 ? "Large circle" : "Circle with radius " + c.radius();
        }
        if (shape instanceof Rectangle r) {
            return r.width() == r.height()
                    ? "Square " + r.width() + "x" + r.height()
                    : "Rectangle " + r.width() + "x" + r.height();
        }
        if (shape instanceof Triangle t) {
            return "Triangle with base " + t.base();
        }
        throw new IllegalStateException("Unknown shape");
    }
}
