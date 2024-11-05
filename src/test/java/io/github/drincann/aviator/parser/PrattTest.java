package io.github.drincann.aviator.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PrattTest {
    @Test
    public void testAddAssociativity() {
        Pratt.Expr expr = Pratt.parse("1 + 2 + 3");
        assertEquals("(+ (+ 1 2) 3)", expr.rp());
    }

    @Test
    public void testSubtractAssociativity() {
        Pratt.Expr expr = Pratt.parse("1 - 2 - 3");
        assertEquals("(- (- 1 2) 3)", expr.rp());
    }

    @Test
    public void test1() {
        Pratt.Expr expr = Pratt.parse("1 + 2 * 3");
        assertEquals("(+ 1 (* 2 3))", expr.rp());
    }

    @Test
    public void test2() {
        Pratt.Expr expr = Pratt.parse("a + b * c * d + e");
        assertEquals("(+ (+ a (* (* b c) d)) e)", expr.rp());
    }

    @Test
    public void testDotAssociativity() {
        Pratt.Expr expr = Pratt.parse("a.b.c.d");
        assertEquals("(. (. (. a b) c) d)", expr.rp());
    }

    @Test
    public void test3() {
        Pratt.Expr expr = Pratt.parse("1 + a.b * c");
        assertEquals("(+ 1 (* (. a b) c))", expr.rp());
    }

    @Test
    public void testSubtractUnary1() {
        Pratt.Expr expr = Pratt.parse("-1");
        assertEquals("(- 1)", expr.rp());
    }

    @Test
    public void testSubtractUnary2() {
        Pratt.Expr expr = Pratt.parse("1 - -2");
        assertEquals("(- 1 (- 2))", expr.rp());
    }

    @Test
    public void testSubtractUnary3() {
        Pratt.Expr expr = Pratt.parse("--1 * 2");
        assertEquals("(* (- (- 1)) 2)", expr.rp());
    }

    @Test
    public void testSubtractUnary4() {
        Pratt.Expr expr = Pratt.parse("--a.b.c");
        assertEquals("(- (- (. (. a b) c)))", expr.rp());
    }

    @Test
    public void testSubtractUnary5() {
        Pratt.Expr expr = Pratt.parse("--1 + 2");
        assertEquals("(+ (- (- 1)) 2)", expr.rp());
    }

    @Test
    public void testSubtractUnary6() {
        Pratt.Expr expr = Pratt.parse("--1 - 2");
        assertEquals("(- (- (- 1)) 2)", expr.rp());
    }

    @Test
    public void testParentheses1() {
        Pratt.Expr expr = Pratt.parse("(1 + 2) * 3");
        assertEquals("(* (+ 1 2) 3)", expr.rp());
    }

    @Test
    public void testParentheses2() {
        Pratt.Expr expr = Pratt.parse("(a + b).c + d");
        assertEquals("(+ (. (+ a b) c) d)", expr.rp());
    }

    @Test
    public void testFieldAccess1() {
        Pratt.Expr expr = Pratt.parse("a[1][2][3]");
        assertEquals("([ ([ ([ a 1) 2) 3)", expr.rp());
    }

    @Test
    public void testFieldAccess2() {
        Pratt.Expr expr = Pratt.parse("a.b['c']['d'].e");
        assertEquals("(. ([ ([ (. a b) c) d) e)", expr.rp());
    }

    @Test
    public void testTernary1() {
        Pratt.Expr expr = Pratt.parse("a ? b : c ? d : e");
        assertEquals("(? a b (? c d e))", expr.rp());
    }

    @Test
    public void testTernary2() {
        Pratt.Expr expr = Pratt.parse("a ? b ? c : d : e");
        assertEquals("(? a (? b c d) e)", expr.rp());
    }

    @Test
    public void testFunctionCall1() {
        Pratt.Expr expr = Pratt.parse("f(1, 2, 3)");
        assertEquals("(f 1 2 3)", expr.rp());
    }

    @Test
    public void testFunctionCall2() {
        Pratt.Expr expr = Pratt.parse("(lambda (x, y) -> x + y end)(1, 2)");
        assertEquals("(lambda (x y) -> (+ x y) end 1 2)", expr.rp());
    }

    @Test
    public void testFunctionCall3() {
        Pratt.Expr expr = Pratt.parse("lambda (x, y) -> x + y end + 2");
        assertEquals("(+ lambda (x y) -> (+ x y) end 2)", expr.rp());
    }

    @Test
    public void testEquality1() {
        Pratt.Expr expr = Pratt.parse("1 != 2 == 3");
        assertEquals("(== (!= 1 2) 3)", expr.rp());
    }

    @Test
    public void testEquality2() {
        Pratt.Expr expr = Pratt.parse("1 > 2 == 3");
        assertEquals("(== (> 1 2) 3)", expr.rp());
    }
}
