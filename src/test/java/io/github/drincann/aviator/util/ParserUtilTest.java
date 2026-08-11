package io.github.drincann.aviator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.parser.Pratt;
import io.github.drincann.aviator.parser.ast.Expr;

class ParserUtilTest {
    @Test
    void identifiesExpressionsByParsingTheCompleteInput() {
        assertTrue(ParserUtil.isExpression("a == b && c > d"));
        assertTrue(ParserUtil.isExpression("'a;b' == value"));
        assertTrue(ParserUtil.isExpression("text =~ /a;b/"));
        assertTrue(ParserUtil.isExpression("result = score > 10 ? 'high' : 'low'"));

        assertFalse(ParserUtil.isExpression("if score > 10 { return true; }"));
        assertFalse(ParserUtil.isExpression("return true"));
        assertFalse(ParserUtil.isExpression("a == b trailing"));
        assertFalse(ParserUtil.isExpression(null));
    }

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

    @Test
    public void case1() {
        String expr =
                "let a = userSeqFeature.result['16247-16247-16247']; a=b;userSeqFeature != '' && userId != '' && seqAbnormalDeviceTopGramUser == '16247-16247-16247' && double(a.tf) > 0.95 && long(userSeqFeature.totalCount) > 100";
        expr = expr.replaceAll(".*;", "");

        List<Expr> split = ParserUtil.split(Pratt.parse(expr));

        assertEquals(5, split.size());
        assertEquals("(userSeqFeature != '')", split.get(0).toString());
        assertEquals("(userId != '')", split.get(1).toString());
        assertEquals("(seqAbnormalDeviceTopGramUser == '16247-16247-16247')", split.get(2).toString());
        assertEquals("(double(a.tf) > 0.95)", split.get(3).toString());
        assertEquals("(long(userSeqFeature.totalCount) > 100)", split.get(4).toString());
    }
}
