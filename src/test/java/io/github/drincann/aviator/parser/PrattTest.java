package io.github.drincann.aviator.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.FunctionCall;
import io.github.drincann.aviator.parser.ast.LambdaFunction;
import io.github.drincann.aviator.parser.ast.OperatorReference;
import io.github.drincann.aviator.parser.ast.UnpackArgument;

class PrattTest {
    @Test
    void followsArithmeticPrecedenceAndLeftAssociativity() {
        assertRp("1 + 2 * 3", "(+ 1 (* 2 3))");
        assertRp("8 % 3 * 2", "(* (% 8 3) 2)");
        assertRp("8 / 2 / 2", "(/ (/ 8 2) 2)");
    }

    @Test
    void exponentiationIsRightAssociativeAndBindsAboveUnary() {
        assertRp("2 ** 3 ** 2", "(** 2 (** 3 2))");
        assertRp("-2 ** 2", "(- (** 2 2))");
        assertRp("2 ** -1", "(** 2 (- 1))");
        assertRp("~1 ** 2", "(~ (** 1 2))");
    }

    @Test
    void followsShiftBitwiseAndLogicalPrecedence() {
        assertRp("1 << 2 + 1", "(<< 1 (+ 2 1))");
        assertRp("4 | 1 ^ 3 & 1", "(| 4 (^ 1 (& 3 1)))");
        assertRp("a | b && c || d", "(|| (&& (| a b) c) d)");
    }

    @Test
    void matchAndEqualityHaveTheSameLeftAssociativePrecedence() {
        assertRp("'abc' =~ /a.*/ == true", "(== (=~ 'abc' /a.*/) true)");
        assertRp("'abc' =~ /x.*/ != true", "(!= (=~ 'abc' /x.*/) true)");
        assertRp("1 < 2 == true", "(== (< 1 2) true)");
    }

    @Test
    void ternaryIsRightAssociative() {
        assertRp("a ? b : c ? d : e", "(? a b (? c d e))");
        assertRp("a ? b ? c : d : e", "(? a (? b c d) e)");
    }

    @Test
    void parsesDottedNamesAsAviatorIdentifiers() {
        assertRp("1 + user.profile.score * 2", "(+ 1 (* user.profile.score 2))");
        assertEquals("user.profile['score'][0]", Pratt.parse("user.profile['score'][0]").serialize());
    }

    @Test
    void parsesNestedLambdasAndVariadicParameter() {
        Expr expression = Pratt.parse("lambda(x, &args) -> lambda(y) -> x + y + count(args) end end");
        LambdaFunction outer = (LambdaFunction) expression;

        assertEquals(2, outer.getParameters().size());
        assertFalse(outer.getParameters().get(0).isVariadic());
        assertTrue(outer.getParameters().get(1).isVariadic());
        assertTrue(outer.getBody() instanceof LambdaFunction);
    }

    @Test
    void parsesArgumentUnpackingAndOperatorReferences() {
        FunctionCall call = (FunctionCall) Pratt.parse("reduce(*inputs, +, 0)");

        assertTrue(call.getArguments().get(0) instanceof UnpackArgument);
        assertTrue(call.getArguments().get(1) instanceof OperatorReference);
        assertEquals("reduce(*inputs, +, 0)", call.serialize());
    }

    @Test
    void parsesAnonymousFunctionInvocation() {
        Expr expression = Pratt.parse("(lambda(x) -> x + 1 end)(2)");

        assertTrue(expression instanceof FunctionCall);
        assertTrue(((FunctionCall) expression).getFunction() instanceof LambdaFunction);
        assertEquals("lambda (x) -> (x + 1) end(2)", expression.serialize());
    }

    @Test
    void assignmentConsumesTheFullRightHandExpression() {
        assertEquals("(a || (b = ((c && d) || e)))", Pratt.parse("a || b = c && d || e").serialize());
        assertEquals("((a == b) = false)", Pratt.parse("a == b = false").serialize());
    }

    @Test
    void consumesTheWholeInput() {
        assertThrows(AviatorParserException.class, () -> Pratt.parse("1 2"));
        assertThrows(AviatorParserException.class, () -> Pratt.parse("1 +"));
        assertThrows(AviatorParserException.class, () -> Pratt.parse("lambda(x) -> x end end"));
        assertThrows(AviatorParserException.class, () -> Pratt.parse("a..b"));
        assertThrows(AviatorParserException.class, () -> Pratt.parse("/a/flag"));
    }

    @Test
    void rejectsStatementsAtTheExpressionBoundary() {
        assertThrows(AviatorParserException.class, () -> Pratt.parse("let x = 1; x + 1"));
        assertThrows(AviatorParserException.class, () -> Pratt.parse("lambda() -> 1; 2 end"));
        assertThrows(AviatorParserException.class, () -> Pratt.parse("if true { return 1; }"));
    }

    @Test
    void enforcesVariadicParameterRules() {
        assertThrows(AviatorParserException.class, () -> Pratt.parse("lambda(&args, x) -> x end"));
        assertThrows(AviatorParserException.class, () -> Pratt.parse("lambda(#x) -> x end"));
    }

    @Test
    void parsingIsIdempotent() {
        Pratt parser = new Pratt(new io.github.drincann.aviator.lexer.AviatorLexer("a + b"));

        assertTrue(parser.parse() == parser.parse());
    }

    private static void assertRp(String source, String expected) {
        assertEquals(expected, Pratt.parse(source).rp());
    }
}
