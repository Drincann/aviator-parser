package io.github.drincann.aviator.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;

/** Differential tests that execute both the source and the parser output on Aviator 5.4.1. */
class AviatorCompatibilityTest {
    @Test
    void serializedArithmeticAndOperatorExpressionsKeepAviatorSemantics() {
        List<String> expressions = Arrays.asList(
                "1 + 2 * 3",
                "8 % 3 * 2",
                "8 / 2 / 2",
                "2 ** 3 ** 2",
                "-2 ** 2",
                "2 ** -1",
                "1 << 2 + 1",
                "-1 >>> 1",
                "4 | 1 ^ 3 & 1",
                "1 < 2 == true",
                "'abc' =~ /a.*/ == true",
                "'abc' =~ /x.*/ != true",
                "true ? 1 : false ? 2 : 3",
                "false ? 1 : false ? 2 : 3");

        expressions.forEach(expression -> assertEquivalent(expression, Collections.emptyMap()));
    }

    @Test
    void serializedLiteralFormsKeepAviatorTypesAndValues() {
        List<String> expressions = Arrays.asList(
                "1M + 2M",
                "99999999999999999999999N + 1N",
                "0x10 + 1",
                "0xG",
                "0x+1",
                "1e2 + 1",
                "1e-2",
                // Aviator 5.4.1 tokenizes this as 1e + 2 and evaluates it to 3.
                "1e+2",
                ".5 + .25",
                ".e1",
                "'a/b' =~ /a\\/b/",
                "'a\n' =~ /.*\n/",
                "'line1\nline2' == 'line1\nline2'");

        expressions.forEach(expression -> assertEquivalent(expression, Collections.emptyMap()));
    }

    @Test
    void serializedFunctionsLambdasAndCollectionsKeepAviatorSemantics() {
        List<String> expressions = Arrays.asList(
                "seq.list(10, 20)[1]",
                "(lambda(x) -> x + 1 end)(2)",
                "(lambda(&args) -> count(args) end)(1, 2, 3)",
                "(lambda(a, b) -> a + b end)(*seq.list(1, 2))",
                "reduce(seq.list(1, 2, 3), +, 0)",
                "reduce(seq.list(1, 2, 3), *, 1)",
                "(lambda(x) -> lambda(y) -> x + y end end)(1)(2)",
                "map(seq.list(1, 2), lambda(x) -> x + 1 end)",
                "1 ## comment\n + 2");

        expressions.forEach(expression -> assertEquivalent(expression, Collections.emptyMap()));
    }

    @Test
    void serializedUnicodeAndQuotedVariablesResolveTheSameEnvironmentKeys() {
        Map<String, Object> environment = new HashMap<>();
        environment.put("变量", 2L);
        environment.put("foo", 3L);
        environment.put("foo-bar", 4L);

        assertEquivalent("变量 + #foo + #`foo-bar`", environment);
    }

    @Test
    void parserAndAviatorBothRejectTrailingTokens() {
        assertThrows(AviatorParserException.class, () -> Pratt.parse("1 2"));
        assertThrows(ExpressionSyntaxErrorException.class, () -> AviatorEvaluator.compile("1 2"));
    }

    @Test
    void parserAndAviatorBothRejectInvalidLiteralAndOperatorReferenceForms() {
        List<String> expressions = Arrays.asList(
                "1e2N",
                "1eM",
                "'a\\/b'",
                "reduce(seq.list(1, 2), >=, true)",
                "[1, 2]");

        expressions.forEach(expression -> {
            assertThrows(RuntimeException.class, () -> Pratt.parse(expression), expression);
            assertThrows(RuntimeException.class, () -> AviatorEvaluator.compile(expression), expression);
        });
    }

    private static void assertEquivalent(String source, Map<String, Object> environment) {
        String serialized = Pratt.parse(source).serialize();
        Object sourceResult = AviatorEvaluator.execute(source, new HashMap<>(environment));
        Object serializedResult = AviatorEvaluator.execute(serialized, new HashMap<>(environment));

        assertEquals(sourceResult == null ? null : sourceResult.getClass(),
                serializedResult == null ? null : serializedResult.getClass(),
                () -> "Result type changed after parsing: " + source + " -> " + serialized);
        assertEquals(sourceResult, serializedResult,
                () -> "Result changed after parsing: " + source + " -> " + serialized);
    }
}
