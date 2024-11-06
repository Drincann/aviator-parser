package io.github.drincann.aviator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.parser.Pratt;
import io.github.drincann.aviator.parser.ast.Expr;

class ParserUtilTest {
    @Test
    public void testSplitAnd() {
        Expr parsed = Pratt.parse("A == 1 && (B == 2 || C.D == true) && E == 'hello'");
        List<Expr> split = ParserUtil.split(parsed);

        assertEquals(3, split.size());
        assertEquals("(A == 1)", split.get(0).toString());
        assertEquals("((B == 2) || (C.D == true))", split.get(1).toString());
        assertEquals("(E == 'hello')", split.get(2).toString());
    }

    @Test
    public void testSplitOr() {
        Expr parsed = Pratt.parse("A == 1 && (B == 2 || C.D == true) || E == 'hello'");
        List<Expr> split = ParserUtil.split(parsed);

        assertEquals(1, split.size());
        assertEquals("(((A == 1) && ((B == 2) || (C.D == true))) || (E == 'hello'))", split.get(0).toString());
    }
}