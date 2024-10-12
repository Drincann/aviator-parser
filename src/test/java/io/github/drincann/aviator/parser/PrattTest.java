package io.github.drincann.aviator.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PrattTest {
    @Test
    public void testAddAssociativity() {
        Pratt pratt = Pratt.parse("1 + 2 + 3");
        assertEquals("(+ (+ 1 2) 3)", pratt.rp());
    }

    @Test
    public void testSubtractAssociativity() {
        Pratt pratt = Pratt.parse("1 - 2 - 3");
        assertEquals("(- (- 1 2) 3)", pratt.rp());
    }

    @Test
    public void test1() {
        Pratt pratt = Pratt.parse("1 + 2 * 3");
        assertEquals("(+ 1 (* 2 3))", pratt.rp());
    }

    @Test
    public void test2() {
        Pratt pratt = Pratt.parse("a + b * c * d + e");
        assertEquals("(+ (+ a (* (* b c) d)) e)", pratt.rp());
    }

    @Test
    public void testDotAssociativity() {
        Pratt pratt = Pratt.parse("a.b.c.d");
        assertEquals("(. (. (. a b) c) d)", pratt.rp());
    }

    @Test
    public void test3() {
        Pratt pratt = Pratt.parse("1 + a.b * c");
        assertEquals("(+ 1 (* (. a b) c))", pratt.rp());
    }

    @Test
    public void testSubtractUnary1() {
        Pratt pratt = Pratt.parse("-1");
        assertEquals("(- 1)", pratt.rp());
    }

    @Test
    public void testSubtractUnary2() {
        Pratt pratt = Pratt.parse("1 - -2");
        assertEquals("(- 1 (- 2))", pratt.rp());
    }

    @Test
    public void testSubtractUnary3() {
        Pratt pratt = Pratt.parse("--1 * 2");
        assertEquals("(* (- (- 1)) 2)", pratt.rp());
    }

    @Test
    public void testSubtractUnary4() {
        Pratt pratt = Pratt.parse("--a.b.c");
        assertEquals("(- (- (. (. a b) c)))", pratt.rp());
    }

    @Test
    public void testSubtractUnary5() {
        Pratt pratt = Pratt.parse("--1 + 2");
        assertEquals("(+ (- (- 1)) 2)", pratt.rp());
    }

    @Test
    public void testSubtractUnary6() {
        Pratt pratt = Pratt.parse("--1 - 2");
        assertEquals("(- (- (- 1)) 2)", pratt.rp());
    }

    @Test
    public void testParentheses1() {
        Pratt pratt = Pratt.parse("(1 + 2) * 3");
        assertEquals("(* (+ 1 2) 3)", pratt.rp());
    }

    @Test
    public void testParentheses2() {
        Pratt pratt = Pratt.parse("(a + b).c + d");
        assertEquals("(+ (. (+ a b) c) d)", pratt.rp());
    }

    @Test
    public void testFieldAccess1() {
        Pratt pratt = Pratt.parse("a[1][2][3]");
        assertEquals("([ ([ ([ a 1) 2) 3)", pratt.rp());
    }

    @Test
    public void testFieldAccess2() {
        Pratt pratt = Pratt.parse("a.b['c']['d'].e");
        assertEquals("(. ([ ([ (. a b) c) d) e)", pratt.rp());
    }

    @Test
    public void testTernary1() {
        Pratt pratt = Pratt.parse("a ? b : c ? d : e");
        assertEquals("(? a b (? c d e))", pratt.rp());
    }

    @Test
    public void testTernary2() {
        Pratt pratt = Pratt.parse("a ? b ? c : d : e");
        assertEquals("(? a (? b c d) e)", pratt.rp());
    }
}