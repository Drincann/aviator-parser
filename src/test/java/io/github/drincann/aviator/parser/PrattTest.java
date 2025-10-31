package io.github.drincann.aviator.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.parser.ast.Expr;

class PrattTest {
    @Test
    public void testAddAssociativity() {
        Expr expr = Pratt.parse("1 + 2 + 3");
        assertEquals("(+ (+ 1 2) 3)", expr.rp());
    }

    @Test
    public void testSubtractAssociativity() {
        Expr expr = Pratt.parse("1 - 2 - 3");
        assertEquals("(- (- 1 2) 3)", expr.rp());
    }

    @Test
    public void test1() {
        Expr expr = Pratt.parse("1 + 2 * 3");
        assertEquals("(+ 1 (* 2 3))", expr.rp());
    }

    @Test
    public void test2() {
        Expr expr = Pratt.parse("a + b * c * d + e");
        assertEquals("(+ (+ a (* (* b c) d)) e)", expr.rp());
    }

    @Test
    public void testDotAssociativity() {
        Expr expr = Pratt.parse("a.b.c.d");
        assertEquals("(. (. (. a b) c) d)", expr.rp());
    }

    @Test
    public void test3() {
        Expr expr = Pratt.parse("1 + a.b * c");
        assertEquals("(+ 1 (* (. a b) c))", expr.rp());
    }

    @Test
    public void testSubtractUnary1() {
        Expr expr = Pratt.parse("-1");
        assertEquals("(- 1)", expr.rp());
    }

    @Test
    public void testSubtractUnary2() {
        Expr expr = Pratt.parse("1 - -2");
        assertEquals("(- 1 (- 2))", expr.rp());
    }

    @Test
    public void testSubtractUnary3() {
        Expr expr = Pratt.parse("--1 * 2");
        assertEquals("(* (- (- 1)) 2)", expr.rp());
    }

    @Test
    public void testSubtractUnary4() {
        Expr expr = Pratt.parse("--a.b.c");
        assertEquals("(- (- (. (. a b) c)))", expr.rp());
    }

    @Test
    public void testSubtractUnary5() {
        Expr expr = Pratt.parse("--1 + 2");
        assertEquals("(+ (- (- 1)) 2)", expr.rp());
    }

    @Test
    public void testSubtractUnary6() {
        Expr expr = Pratt.parse("--1 - 2");
        assertEquals("(- (- (- 1)) 2)", expr.rp());
    }

    @Test
    public void testParentheses1() {
        Expr expr = Pratt.parse("(1 + 2) * 3");
        assertEquals("(* (+ 1 2) 3)", expr.rp());
    }

    @Test
    public void testParentheses2() {
        Expr expr = Pratt.parse("(a + b).c + d");
        assertEquals("(+ (. (+ a b) c) d)", expr.rp());
    }

    @Test
    public void testFieldAccess1() {
        Expr expr = Pratt.parse("a[1][2][3]");
        assertEquals("([ ([ ([ a 1) 2) 3)", expr.rp());
    }

    @Test
    public void testFieldAccess2() {
        Expr expr = Pratt.parse("a.b['c']['d'].e");
        assertEquals("(. ([ ([ (. a b) 'c') 'd') e)", expr.rp());
    }

    @Test
    public void testTernary1() {
        Expr expr = Pratt.parse("a ? b : c ? d : e");
        assertEquals("(? a b (? c d e))", expr.rp());
    }

    @Test
    public void testTernary2() {
        Expr expr = Pratt.parse("a ? b ? c : d : e");
        assertEquals("(? a (? b c d) e)", expr.rp());
    }

    @Test
    public void testFunctionCall1() {
        Expr expr = Pratt.parse("f(1, 2, 3)");
        assertEquals("(f 1 2 3)", expr.rp());
    }

    @Test
    public void testFunctionCall2() {
        Expr expr = Pratt.parse("(lambda (x, y) -> x + y end)(1, 2)");
        assertEquals("(lambda (x y) -> (+ x y) end 1 2)", expr.rp());
    }

    @Test
    public void testFunctionCall3() {
        Expr expr = Pratt.parse("lambda (x, y) -> x + y end + 2");
        assertEquals("(+ lambda (x y) -> (+ x y) end 2)", expr.rp());
    }

    @Test
    public void testEquality1() {
        Expr expr = Pratt.parse("1 != 2 == 3");
        assertEquals("(== (!= 1 2) 3)", expr.rp());
    }

    @Test
    public void testEquality2() {
        Expr expr = Pratt.parse("1 > 2 == 3");
        assertEquals("(== (> 1 2) 3)", expr.rp());
    }

    @Test
    public void testUseCase1() {
        Expr expr = Pratt.parse("fun(\"\\\"\")");
        assertEquals("(fun \"\\\"\")", expr.rp());
    }

    @Test
    public void testObjectAccessStringify() {
        Expr expr = Pratt.parse("a.b[c]['d'].e");
        assertEquals("a.b[c]['d'].e", expr.toString());
    }

    @Test
    public void testRegex() {
        Expr expr = Pratt.parse("phoneWithCountryCode =~ /86162.*/");
        assertEquals("(=~ phoneWithCountryCode /86162.*/)", expr.rp());
    }

    @Test
    public void testMod() {
        Expr expr = Pratt.parse("(tinySignResult == 'paramMissing' || tinySignResult == 'fail')&&endpoint=='api.sns.v1.note.imagefeed.get'&&isGuest=='true' && !(baseGoodTinyMissCnt1HQuery < baseGoodCnt1HQuery*0.3 && endpoint == 'api.sns.v1.note.imagefeed.get' && isTinyMissLastOneHour == 1 && isGuest == 'true' && ( ((appMainGuestTinyMissCnt1H > 3000 || (appMainGuestTinyMissCnt1H > 600 && appMainGuestTinyMissCnt1H/appMainCnt1H > 0.4)) && (getCurrentHour>8 || getCurrentHour<1)) || ((appMainGuestTinyMissCnt1H > 1000 || (appMainGuestTinyMissCnt1H > 1000 && appMainGuestTinyMissCnt1H/appMainCnt1H > 0.2)) && getCurrentHour<=8 && getCurrentHour>=1) )) && !(endpoint == 'api.sns.v1.note.imagefeed.get' && isGuest == 'true' && ((androidDeviceCheckRisk==true&&platform=='android')||(iosDeviceCheckRisk==true&&platform=='ios')) && (riskByBuildCnt1H > appMainNoteByBuildCnt1H*0.7) && riskByBuildCnt1H > 400 && (baseRiskDidCnt1HQuery < baseGoodCnt1HQuery*0.08 && baseGoodCnt1HQuery > 500))");
        System.out.println(expr);
    }


    @Test
    public void testAssign() {
        Expr expr = Pratt.parse("a || b = c && d || e");
        Expr expr1 = Pratt.parse("a && b = c && d || e");
        Expr expr2 = Pratt.parse("'' =~ b = /a/");
        Expr expr3 = Pratt.parse("a == b = false");
        assertEquals(expr.toString(),  "(a || (b = ((c && d) || e)))");
        assertEquals(expr1.toString(), "(a && (b = ((c && d) || e)))");
        assertEquals(expr2.toString(), "(('' =~ b) = /a/)");
        assertEquals(expr3.toString(), "((a == b) = false)");
    }
}
