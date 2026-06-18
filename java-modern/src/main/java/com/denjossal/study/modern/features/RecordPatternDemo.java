package com.denjossal.study.modern.features;

/**
 * Demonstrates pattern matching with instanceof (Java 16) and records.
 * Record patterns (deconstruction in switch) require Java 21;
 * this version uses instanceof patterns which work on Java 16+.
 */
public class RecordPatternDemo {

    public sealed interface Expr permits Num, Add, Mul {}
    public record Num(int value) implements Expr {}
    public record Add(Expr left, Expr right) implements Expr {}
    public record Mul(Expr left, Expr right) implements Expr {}

    /**
     * Evaluates an expression tree using instanceof pattern matching.
     */
    public static int evaluate(Expr expr) {
        if (expr instanceof Num n) return n.value();
        if (expr instanceof Add a) return evaluate(a.left()) + evaluate(a.right());
        if (expr instanceof Mul m) return evaluate(m.left()) * evaluate(m.right());
        throw new IllegalStateException("Unknown expr type");
    }

    /**
     * Pretty-prints an expression tree.
     */
    public static String format(Expr expr) {
        if (expr instanceof Num n) return String.valueOf(n.value());
        if (expr instanceof Add a) return "(%s + %s)".formatted(format(a.left()), format(a.right()));
        if (expr instanceof Mul m) return "(%s * %s)".formatted(format(m.left()), format(m.right()));
        throw new IllegalStateException("Unknown expr type");
    }

    public record Point(int x, int y) {}
    public record Line(Point start, Point end) {}

    /**
     * Uses instanceof pattern matching with records.
     */
    public static double length(Line line) {
        Point s = line.start();
        Point e = line.end();
        return Math.sqrt(Math.pow(e.x() - s.x(), 2) + Math.pow(e.y() - s.y(), 2));
    }
}
