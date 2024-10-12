package io.github.drincann.aviator.lexer;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ADD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EOF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MULTIPLY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NUMBER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.REGEX;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;


class AviatorLexerTest {
    @Test
    public void testValidIdentifier() {
        AviatorLexer lexer = new AviatorLexer("a _a _a1 a1");

        assertEquals("a", lexer.next().getLexeme());
        assertEquals("_a", lexer.next().getLexeme());
        assertEquals("_a1", lexer.next().getLexeme());
        assertEquals("a1", lexer.next().getLexeme());
    }

    @Test
    public void testObjectAccess() {
        AviatorLexer
                lexer = new AviatorLexer("a.b['1']['2'].e");
        AviatorToken token = lexer.next();
        assertEquals("a", token.getLexeme());
        assertEquals(AviatorTokenType.IDENTIFIER, token.getType());

        token = lexer.next();
        assertEquals(".", token.getLexeme());
        assertEquals(AviatorTokenType.DOT, token.getType());

        token = lexer.next();
        assertEquals("b", token.getLexeme());
        assertEquals(AviatorTokenType.IDENTIFIER, token.getType());

        token = lexer.next();
        assertEquals("[", token.getLexeme());
        assertEquals(AviatorTokenType.LEFT_BRACKET, token.getType());

        token = lexer.next();
        assertEquals("1", token.getLexeme());
        assertEquals(AviatorTokenType.STRING, token.getType());

        token = lexer.next();
        assertEquals("]", token.getLexeme());
        assertEquals(AviatorTokenType.RIGHT_BRACKET, token.getType());

        token = lexer.next();
        assertEquals("[", token.getLexeme());
        assertEquals(AviatorTokenType.LEFT_BRACKET, token.getType());

        token = lexer.next();
        assertEquals("2", token.getLexeme());
        assertEquals(AviatorTokenType.STRING, token.getType());

        token = lexer.next();
        assertEquals("]", token.getLexeme());
        assertEquals(AviatorTokenType.RIGHT_BRACKET, token.getType());

        token = lexer.next();
        assertEquals(".", token.getLexeme());
        assertEquals(AviatorTokenType.DOT, token.getType());

        token = lexer.next();
        assertEquals("e", token.getLexeme());
        assertEquals(AviatorTokenType.IDENTIFIER, token.getType());

        assertEquals(EOF, lexer.next().getType());
    }

    @Test
    public void testInvalidIdentifier() {
        assertNotEquals("1a", new AviatorLexer("1a").next().getLexeme());
    }

    @Test
    public void testValidNumber() {
        String code =
                "123 0123 0x0123456789abcdef 0X123" +
                        " 01.23 0.123" +
                        " 1.23e10 1.23e-10" +
                        " 1.23E10 1.23E-10" +
                        " 1.23e010 1.23e-010" +
                        " 1.23E010 1.23E-010" +
                        " .e1 .1e1 .e-1 .1e-1";
        AviatorLexer
                lexer = new AviatorLexer(code);

        for (String numberLiteral : code.split(" ")) {
            AviatorToken token = lexer.next();
            assertNotNull(token);
            assertEquals(NUMBER, token.getType());
            assertEquals(numberLiteral, token.getLexeme());
        }

        assertEquals(EOF, lexer.next().getType());
    }

    @Test
    public void testValidRegexLiteral() {
        assertEquals("[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(",
                new AviatorLexer("/[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(/").next()
                        .getLexeme());
        assertEquals("", new AviatorLexer("//").next().getLexeme());
    }

    @Test
    public void testInvalidMultilineRegex() {
        assertThrows(
                AviatorLexerException.class,
                () -> new AviatorLexer("/.*\n/").next());
    }

    @Test
    public void testValidKeywords() {
        String code =
                "if else elsif for in while break continue return " +
                        "try catch finally throw " +
                        "fn lambda -> end " +
                        "true false nil" +
                        "let new use ";
        AviatorLexer
                lexer = new AviatorLexer(code);

        for (String keyword : code.split(" ")) {
            AviatorToken token = lexer.next();
            assertNotNull(token);
            assertEquals(keyword, token.getLexeme());
        }

        assertEquals(EOF, lexer.next().getType());
    }

    @Test
    public void testInvalidMultilineKeywords() {
        assertEquals("i", new AviatorLexer("i\nf").next().getLexeme());
    }

    @Test
    public void testValidArithmeticOperators() {
        String code = "~ 1 + 1 - 1 * 1 / 1 % 1 & 1 | 1 ^ 1 < ~ 1 > 1 <= 1 >= 1 ** 10 >> 2 << 1 >>> 1";
        AviatorLexer lexer = new AviatorLexer(code);
        AviatorToken tokenBitNot = lexer.next();
        assertEquals("~", tokenBitNot.getLexeme());
        assertEquals(BIT_NOT, tokenBitNot.getType());
        assertEquals(0, tokenBitNot.getStart());
        assertEquals(1, tokenBitNot.getEnd());
        assertEquals(1, tokenBitNot.getLine());

        AviatorToken tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(2, tokenNumber.getStart());
        assertEquals(3, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenAdd = lexer.next();
        assertEquals("+", tokenAdd.getLexeme());
        assertEquals(ADD, tokenAdd.getType());
        assertEquals(4, tokenAdd.getStart());
        assertEquals(5, tokenAdd.getEnd());
        assertEquals(1, tokenAdd.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(6, tokenNumber.getStart());
        assertEquals(7, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenSubtract = lexer.next();
        assertEquals("-", tokenSubtract.getLexeme());
        assertEquals(SUBTRACT, tokenSubtract.getType());
        assertEquals(8, tokenSubtract.getStart());
        assertEquals(9, tokenSubtract.getEnd());
        assertEquals(1, tokenSubtract.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(10, tokenNumber.getStart());
        assertEquals(11, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenMultiply = lexer.next();
        assertEquals("*", tokenMultiply.getLexeme());
        assertEquals(MULTIPLY, tokenMultiply.getType());
        assertEquals(12, tokenMultiply.getStart());
        assertEquals(13, tokenMultiply.getEnd());
        assertEquals(1, tokenMultiply.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(14, tokenNumber.getStart());
        assertEquals(15, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenDivide = lexer.next();
        assertEquals("/", tokenDivide.getLexeme());
        assertEquals(DIVIDE, tokenDivide.getType());
        assertEquals(16, tokenDivide.getStart());
        assertEquals(17, tokenDivide.getEnd());
        assertEquals(1, tokenDivide.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(18, tokenNumber.getStart());
        assertEquals(19, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenMod = lexer.next();
        assertEquals("%", tokenMod.getLexeme());
        assertEquals(AviatorTokenType.MOD, tokenMod.getType());
        assertEquals(20, tokenMod.getStart());
        assertEquals(21, tokenMod.getEnd());
        assertEquals(1, tokenMod.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(22, tokenNumber.getStart());
        assertEquals(23, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenBitAnd = lexer.next();
        assertEquals("&", tokenBitAnd.getLexeme());
        assertEquals(AviatorTokenType.BIT_AND, tokenBitAnd.getType());
        assertEquals(24, tokenBitAnd.getStart());
        assertEquals(25, tokenBitAnd.getEnd());
        assertEquals(1, tokenBitAnd.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(26, tokenNumber.getStart());
        assertEquals(27, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenBitOr = lexer.next();
        assertEquals("|", tokenBitOr.getLexeme());
        assertEquals(AviatorTokenType.BIT_OR, tokenBitOr.getType());
        assertEquals(28, tokenBitOr.getStart());
        assertEquals(29, tokenBitOr.getEnd());
        assertEquals(1, tokenBitOr.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(30, tokenNumber.getStart());
        assertEquals(31, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenBitXor = lexer.next();
        assertEquals("^", tokenBitXor.getLexeme());
        assertEquals(AviatorTokenType.BIT_XOR, tokenBitXor.getType());
        assertEquals(32, tokenBitXor.getStart());
        assertEquals(33, tokenBitXor.getEnd());
        assertEquals(1, tokenBitXor.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(34, tokenNumber.getStart());
        assertEquals(35, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenLessThan = lexer.next();
        assertEquals("<", tokenLessThan.getLexeme());
        assertEquals(AviatorTokenType.LESS_THAN, tokenLessThan.getType());
        assertEquals(36, tokenLessThan.getStart());
        assertEquals(37, tokenLessThan.getEnd());
        assertEquals(1, tokenLessThan.getLine());

        tokenBitNot = lexer.next();
        assertEquals("~", tokenBitNot.getLexeme());
        assertEquals(BIT_NOT, tokenBitNot.getType());
        assertEquals(38, tokenBitNot.getStart());
        assertEquals(39, tokenBitNot.getEnd());
        assertEquals(1, tokenBitNot.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(40, tokenNumber.getStart());
        assertEquals(41, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenGreaterThan = lexer.next();
        assertEquals(">", tokenGreaterThan.getLexeme());
        assertEquals(AviatorTokenType.GREATER_THAN, tokenGreaterThan.getType());
        assertEquals(42, tokenGreaterThan.getStart());
        assertEquals(43, tokenGreaterThan.getEnd());
        assertEquals(1, tokenGreaterThan.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(44, tokenNumber.getStart());
        assertEquals(45, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenLessThanEqual = lexer.next();
        assertEquals("<=", tokenLessThanEqual.getLexeme());
        assertEquals(AviatorTokenType.LESS_THAN_EQUAL, tokenLessThanEqual.getType());
        assertEquals(46, tokenLessThanEqual.getStart());
        assertEquals(48, tokenLessThanEqual.getEnd());
        assertEquals(1, tokenLessThanEqual.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(49, tokenNumber.getStart());
        assertEquals(50, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenGreaterThanEqual = lexer.next();
        assertEquals(">=", tokenGreaterThanEqual.getLexeme());
        assertEquals(AviatorTokenType.GREATER_THAN_EQUAL, tokenGreaterThanEqual.getType());
        assertEquals(51, tokenGreaterThanEqual.getStart());
        assertEquals(53, tokenGreaterThanEqual.getEnd());
        assertEquals(1, tokenGreaterThanEqual.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(54, tokenNumber.getStart());
        assertEquals(55, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenPow = lexer.next();
        assertEquals("**", tokenPow.getLexeme());
        assertEquals(AviatorTokenType.POW, tokenPow.getType());
        assertEquals(56, tokenPow.getStart());
        assertEquals(58, tokenPow.getEnd());
        assertEquals(1, tokenPow.getLine());

        tokenNumber = lexer.next();
        assertEquals("10", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(59, tokenNumber.getStart());
        assertEquals(61, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenShiftRight = lexer.next();
        assertEquals(">>", tokenShiftRight.getLexeme());
        assertEquals(AviatorTokenType.SIGNED_BIT_SHIFT_RIGHT, tokenShiftRight.getType());
        assertEquals(62, tokenShiftRight.getStart());
        assertEquals(64, tokenShiftRight.getEnd());
        assertEquals(1, tokenShiftRight.getLine());

        tokenNumber = lexer.next();
        assertEquals("2", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(65, tokenNumber.getStart());
        assertEquals(66, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenShiftLeft = lexer.next();
        assertEquals("<<", tokenShiftLeft.getLexeme());
        assertEquals(AviatorTokenType.SIGNED_BIT_SHIFT_LEFT, tokenShiftLeft.getType());
        assertEquals(67, tokenShiftLeft.getStart());
        assertEquals(69, tokenShiftLeft.getEnd());
        assertEquals(1, tokenShiftLeft.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(70, tokenNumber.getStart());
        assertEquals(71, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        AviatorToken tokenUnsignedShiftRight = lexer.next();
        assertEquals(">>>", tokenUnsignedShiftRight.getLexeme());
        assertEquals(AviatorTokenType.UNSIGNED_BIT_SHIFT_RIGHT, tokenUnsignedShiftRight.getType());
        assertEquals(72, tokenUnsignedShiftRight.getStart());
        assertEquals(75, tokenUnsignedShiftRight.getEnd());
        assertEquals(1, tokenUnsignedShiftRight.getLine());

        tokenNumber = lexer.next();
        assertEquals("1", tokenNumber.getLexeme());
        assertEquals(NUMBER, tokenNumber.getType());
        assertEquals(76, tokenNumber.getStart());
        assertEquals(77, tokenNumber.getEnd());
        assertEquals(1, tokenNumber.getLine());

        assertEquals(EOF, lexer.next().getType());
    }

    @Test
    public void testDivideAndRegexAmbiguity1() {
        AviatorLexer lexer = new AviatorLexer("let a = /1/");
        assertEquals("let", lexer.next().getLexeme());
        assertEquals("a", lexer.next().getLexeme());
        assertEquals("=", lexer.next().getLexeme());
        AviatorToken regex = lexer.next();
        assertEquals("1", regex.getLexeme());
        assertEquals(REGEX, regex.getType());
    }

    @Test
    public void testDivideAndRegexAmbiguity2() {
        AviatorLexer lexer = new AviatorLexer("let a = 1 / 2");
        assertEquals("let", lexer.next().getLexeme());
        assertEquals("a", lexer.next().getLexeme());
        assertEquals("=", lexer.next().getLexeme());
        assertEquals("1", lexer.next().getLexeme());
        assertEquals("/", lexer.next().getLexeme());
        assertEquals("2", lexer.next().getLexeme());
    }

    @Test
    public void testDivideAndRegexAmbiguity3() {
        AviatorLexer lexer = new AviatorLexer("fn(){}/1/");
        assertEquals("fn", lexer.next().getLexeme());
        assertEquals("(", lexer.next().getLexeme());
        assertEquals(")", lexer.next().getLexeme());
        assertEquals("{", lexer.next().getLexeme());
        assertEquals("}", lexer.next().getLexeme());
        AviatorToken regex = lexer.next();
        assertEquals("1", regex.getLexeme());
        assertEquals(REGEX, regex.getType());
    }

    @Test
    public void testDivideAndRegexAmbiguity4() {
        AviatorLexer lexer = new AviatorLexer("a[2]/1/");
        assertEquals("a", lexer.next().getLexeme());
        assertEquals("[", lexer.next().getLexeme());
        assertEquals("2", lexer.next().getLexeme());
        assertEquals("]", lexer.next().getLexeme());
        assertEquals("/", lexer.next().getLexeme());
        assertEquals("1", lexer.next().getLexeme());
        assertEquals("/", lexer.next().getLexeme());
    }

    @Test
    public void testNormalStringLiteral() {
        AviatorLexer lexer = new AviatorLexer("\"hello world\"");
        assertEquals("hello world", lexer.next().getLexeme());
    }

    @Test
    public void testInvalidNotClosedStringLiteral1() {
        AviatorLexer lexer = new AviatorLexer("\"hello world");
        assertThrows(AviatorLexerException.class, lexer::next);
    }

    @Test
    public void testInvalidNotClosedStringLiteral2() {
        AviatorLexer lexer = new AviatorLexer("\"hello world\n");
        assertThrows(AviatorLexerException.class, lexer::next);
    }
}
