package io.github.drincann.aviator.lexer;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EOF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IDENTIFIER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LIKE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NUMBER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.POW;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.REGEX;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;

class AviatorLexerTest {
    @Test
    void lexesUnicodeDottedAndQuotedIdentifiers() {
        List<AviatorToken> tokens = lex("变量 user.profile #foo #`foo-bar`");

        assertToken(tokens.get(0), IDENTIFIER, "变量");
        assertToken(tokens.get(1), IDENTIFIER, "user.profile");
        assertToken(tokens.get(2), IDENTIFIER, "#foo");
        assertEquals("foo", tokens.get(2).getValue());
        assertToken(tokens.get(3), IDENTIFIER, "#`foo-bar`");
        assertEquals("foo-bar", tokens.get(3).getValue());
    }

    @Test
    void lexesAllAviatorNumberFormsWithoutLosingSuffixes() {
        String code = "123 0x10 1.25 1e2 1e-2 1e+2 .5 .e1 1M 99999999999999999999999N";
        List<AviatorToken> tokens = lex(code);

        assertEquals(
                Arrays.asList("123", "0x10", "1.25", "1e2", "1e-2", "1e", "+", "2",
                        ".5", ".e1", "1M", "99999999999999999999999N", ""),
                tokens.stream().map(AviatorToken::getLexeme).collect(Collectors.toList()));
        assertEquals(NUMBER, tokens.get(9).getType());
    }

    @Test
    void keepsAviatorHexLiteralTokenBoundaries() {
        assertToken(new AviatorLexer("0x").next(), NUMBER, "0x");
        assertToken(new AviatorLexer("0xG").next(), NUMBER, "0xG");
        assertToken(new AviatorLexer("0x+1").next(), NUMBER, "0x+1");
        assertToken(new AviatorLexer("0x10").next(), NUMBER, "0x10");
    }

    @Test
    void rejectsMalformedNumberInsteadOfReturningAPartialToken() {
        assertThrows(AviatorLexerException.class, () -> lex("1.2.3"));
        assertThrows(AviatorLexerException.class, () -> lex("1.0N"));
        assertThrows(AviatorLexerException.class, () -> lex("1e2N"));
        assertThrows(AviatorLexerException.class, () -> lex("1eM"));
    }

    @Test
    void lexesMultilineStringsAndValidEscapes() {
        AviatorToken token = new AviatorLexer("'first\\nsecond\nthird'").next();

        assertToken(token, STRING, "'first\\nsecond\nthird'");
        assertEquals(1, token.getLine());
    }

    @Test
    void rejectsUnsupportedStringEscape() {
        assertThrows(AviatorLexerException.class, () -> new AviatorLexer("'a\\/b'").next());
    }

    @Test
    void distinguishesDivisionFromRegexAndHandlesEscapedSlash() {
        List<AviatorToken> tokens = lex("'a/b' =~ /a\\/b/ && 8 / 2");

        assertEquals(STRING, tokens.get(0).getType());
        assertEquals(LIKE, tokens.get(1).getType());
        assertToken(tokens.get(2), REGEX, "/a\\/b/");
        assertEquals(DIVIDE, tokens.get(5).getType());
    }

    @Test
    void lexesMultilineRegexLikeAviator() {
        AviatorToken token = new AviatorLexer("/.*(\n  a|b\n).*/").next();

        assertToken(token, REGEX, "/.*(\n  a|b\n).*/");
    }

    @Test
    void treatsSlashFollowedByArgumentDelimiterAsOperatorReference() {
        List<AviatorToken> tokens = lex("reduce(xs, /, 1)");

        assertEquals(DIVIDE, tokens.get(4).getType());
    }

    @Test
    void acceptsWhitespaceAndCommentsInsideCompoundOperators() {
        List<AviatorToken> tokens = lex("1 < ## comment\n = 2 && 2 * * 3");

        assertToken(tokens.get(1), LESS_THAN_EQUAL, "<=");
        assertEquals(1, tokens.get(1).getLine());
        assertToken(tokens.get(5), POW, "**");
    }

    @Test
    void skipsOnlyDoubleHashComments() {
        List<AviatorToken> tokens = lex("1 ## comment\n + #factor");

        assertEquals(Arrays.asList("1", "+", "#factor", ""),
                tokens.stream().map(AviatorToken::getLexeme).collect(Collectors.toList()));
    }

    @Test
    void reportsUnknownCharacters() {
        assertThrows(AviatorLexerException.class, () -> lex("a @ b"));
    }

    @Test
    void tracksSourceOffsetsAndLines() {
        List<AviatorToken> tokens = lex("a\n  <= b");
        AviatorToken operator = tokens.get(1);

        assertEquals(4, operator.getStart());
        assertEquals(6, operator.getEnd());
        assertEquals(2, operator.getLine());
        assertEquals(EOF, tokens.get(tokens.size() - 1).getType());
    }

    private static List<AviatorToken> lex(String code) {
        AviatorLexer lexer = new AviatorLexer(code);
        java.util.ArrayList<AviatorToken> tokens = new java.util.ArrayList<>();
        AviatorToken token;
        do {
            token = lexer.next();
            tokens.add(token);
        } while (token.getType() != EOF);
        return tokens;
    }

    private static void assertToken(AviatorToken token, AviatorTokenType type, String lexeme) {
        assertEquals(type, token.getType());
        assertEquals(lexeme, token.getLexeme());
    }
}
