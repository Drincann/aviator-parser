package io.github.drincann.aviator.lexer;

import static io.github.drincann.aviator.lexer.token.AviatorToken.KEYWORDS_MAP;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ADD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ARROW;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_AND;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_OR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_XOR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COLON;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COMMA;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONDITIONAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EOF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IDENTIFIER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_AND;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_OR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MOD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MULTIPLY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NOT_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NUMBER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.POW;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.REGEX;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RIGHT_BRACE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RIGHT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RIGHT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SEMICOLON;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SIGNED_BIT_SHIFT_LEFT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SIGNED_BIT_SHIFT_RIGHT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.UNSIGNED_BIT_SHIFT_RIGHT;
import static io.github.drincann.aviator.util.CollectionUtil.map;
import static io.github.drincann.aviator.util.CollectionUtil.set;
import static io.github.drincann.aviator.util.LexerUtil.isDigit;
import static io.github.drincann.aviator.util.LexerUtil.isIdentifierRest;
import static io.github.drincann.aviator.util.LexerUtil.isIdentifierStart;
import static io.github.drincann.aviator.util.LexerUtil.isNotEOL;
import static io.github.drincann.aviator.util.LexerUtil.isNotIdentifierStart;
import static io.github.drincann.aviator.util.LexerUtil.isStringLiteralStart;
import static io.github.drincann.aviator.util.LexerUtil.toPrintable;
import static java.lang.String.format;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.util.LexerUtil;

@SuppressWarnings("checkstyle:all")
public class AviatorLexer {
    private final String code;
    private final LexerState state = new LexerState();
    private int cursor = -1; // cursor 指向当前处理的字符
    private int line = 1; // 行数从 1 开始
    private int column = -1; // 列数从 0 开始
    private AviatorToken lastToken = eof();

    public class LexerState {
        public final Map<AviatorTokenType, Set<AviatorTokenType>> validTransferFrom = map(
                DIVIDE, set(
                        NUMBER, IDENTIFIER, RIGHT_PAREN, RIGHT_BRACKET
                ),
                DOT, set(
                        IDENTIFIER, RIGHT_BRACKET, RIGHT_PAREN
                )
        );

        public final Map<AviatorTokenType, Set<AviatorTokenType>> invalidTransferFrom = map(
                NUMBER, set(
                        RIGHT_BRACKET, RIGHT_PAREN
                )
        );

        public boolean expect(AviatorTokenType type) {
            if (validTransferFrom.containsKey(type)) {
                return validTransferFrom.get(type).contains(lastToken.getType());
            }
            if (invalidTransferFrom.containsKey(type)) {
                return !invalidTransferFrom.get(type).contains(lastToken.getType());
            }

            return true;
        }
    }

    public AviatorLexer(String code) {
        this.code = code;
    }

    /**
     * 获取下一个 Token
     * 每产生一个 token，cursor 应该指向该 token 的最后一个字符
     * @return null if no more token
     */
    public AviatorToken next() {
        while (hasNext()) {
            char ch = nextChar(); // 指向下一个 token 的第一个字符
            if (ch == '\n') {
                newLine();
                continue;
            }

            if (isCommentStart()) {
                untilMeet('\n');
                continue;
            }

            if (isNumberLiteralStart()) {
                if (state.expect(NUMBER)) {
                    return parseNextNumberLiteral();
                } // or parse to dot
            }
            if (isIdentifierStart(ch)) {
                return parseNextIdentifier();
            }
            if (isStringLiteralStart(ch)) {
                return parseNextStringLiteral();
            }

            if (ch == '&') {
                return parseNextAnd();
            }
            if (ch == '|') {
                return parseNextOr();
            }
            if (ch == '<') {
                return parseNextLessOrBitShiftLeft();
            }
            if (ch == '>') {
                return parseNextGreaterOrBitShiftRight();
            }
            if (ch == '=') {
                return parseNextEqualLikeOrAssign();
            }
            if (ch == '!') {
                return parseNextNotOrNotEqual();
            }

            if (ch == '+') {
                return token(ADD, "+");
            }
            if (ch == '-') {
                return tokenSubtractOrArrow();
            }
            if (ch == '*') {
                return parseMultiplyOrPower();
            }
            if (ch == '/') {
                if (state.expect(DIVIDE)) {
                    return token(DIVIDE, "/");
                }
                if (state.expect(REGEX)) {
                    return parseNextRegexLiteral();
                }
            }
            if (ch == '%') {
                return token(MOD, "%");
            }
            if (ch == '^') {
                return token(BIT_XOR, "^");
            }
            if (ch == '~') {
                return token(BIT_NOT, "~");
            }

            if (ch == '(') {
                return token(LEFT_PAREN, "(");
            }
            if (ch == ')') {
                return token(RIGHT_PAREN, ")");
            }
            if (ch == '[') {
                return token(LEFT_BRACKET, "[");
            }
            if (ch == ']') {
                return token(RIGHT_BRACKET, "]");
            }
            if (ch == '{') {
                return token(LEFT_BRACE, "{");
            }
            if (ch == '}') {
                return token(RIGHT_BRACE, "}");
            }
            if (ch == '.') {
                return token(DOT, ".");
            }
            if (ch == '?') {
                return token(CONDITIONAL, "?");
            }
            if (ch == ':') {
                return token(COLON, ":");
            }
            if (ch == ',') {
                return token(COMMA, ",");
            }
            if (ch == ';') {
                return token(SEMICOLON, ";");
            }
        }
        return eof();
    }

    private AviatorToken eof() {
        return lastToken = new AviatorToken()
                .setType(EOF)
                .setLexeme("")
                .setStart(cursor + 1).setEnd(cursor + 1).setLine(line);
    }

    private boolean isNumberLiteralStart() {
        return // '0.00e-00' | '0x00'
                isDigit(currentChar())
                        // '.00e-00'
                        || currentChar() == '.' && isDigit(peek())
                        // '.e'
                        || currentChar() == '.' && (peek() == 'e' || peek() == 'E');
    }

    private AviatorToken parseNextAnd() {
        if (peek() == '&') {
            nextChar();
            return token(LOGIC_AND, "&&");
        }
        return token(BIT_AND, "&");
    }

    private AviatorToken parseNextOr() {
        if (peek() == '|') {
            nextChar();
            return token(LOGIC_OR, "||");
        }

        return token(BIT_OR, "|");
    }

    private AviatorToken parseNextLessOrBitShiftLeft() {
        if (peek() == '=') {
            nextChar();
            return token(LESS_THAN_EQUAL, "<=");
        }

        if (peek() == '<') {
            nextChar();
            return token(SIGNED_BIT_SHIFT_LEFT, "<<");
        }

        return token(LESS_THAN, "<");
    }

    /**
     * state:
     * >, >=, >>, >>>
     * |  |   |   |
     * |  |   |   |__cursor
     * |  |   |______cursor
     * |  |__________cursor
     * |_____________cursor
     */
    private AviatorToken parseNextGreaterOrBitShiftRight() {
        if (peek() == '=') { // >=
            nextChar(); //       ^
            return token(GREATER_THAN_EQUAL, ">=");
        }

        if (peek() == '>') { // >>
            nextChar(); //       ^

            if (peek() == '>') { // >>>
                nextChar(); //        ^
                return token(UNSIGNED_BIT_SHIFT_RIGHT, ">>>");
            }

            return token(SIGNED_BIT_SHIFT_RIGHT, ">>");
        }

        return token(GREATER_THAN, ">");
    }

    private AviatorToken parseNextEqualLikeOrAssign() {
        if (peek() == '=') {
            nextChar();
            return token(EQUAL, "==");
        }
        if (peek() == '~') {
            nextChar();
            return token(AviatorTokenType.LIKE, "=~");
        }

        return token(AviatorTokenType.ASSIGN, "=");
    }

    private AviatorToken parseNextNotOrNotEqual() {
        if (peek() == '=') {
            nextChar();
            return token(NOT_EQUAL, "!=");
        }

        return token(LOGIC_NOT, "!");
    }

    private AviatorToken tokenSubtractOrArrow() {
        if (peek() == '>') {
            nextChar();
            return token(ARROW, "->");
        }

        return token(SUBTRACT, "-");
    }

    private AviatorToken parseMultiplyOrPower() {
        if (peek() == '*') {
            nextChar(); // skip first '*'
            return token(POW, "**");
        }

        return token(MULTIPLY, "*");

    }

    /**
     * start:
     * let identifier = 00.00E-00
     *                  ^
     *                  |__start, cursor
     *
     * end:
     * let identifier = 00.00E-00
     *                  ^       ^
     *                  |       |__end, cursor
     *                  |__________start
     */
    private AviatorToken parseNextNumberLiteral() {
        int start = cursor;

        // hex
        if (currentChar() == '0' && (peek() == 'x' || peek() == 'X')) {
            nextChar(); // skip '0'
            nextChar(); // skip 'x'
            untilNonMatch(LexerUtil::isHexDigit); // skip valid hex digit
            return number(start, cursor + 1);
        }

        // dec
        untilNonMatch(LexerUtil::isDigit); // 整数
        if (peek() == '.') { // 小数
            nextChar(); // skip current digit
            nextChar(); // skip '.'
            untilNonMatch(LexerUtil::isDigit); // skip valid digit
        }

        if (peek() == 'e' || peek() == 'E') { // 指数
            nextChar(); // skip current digit
            nextChar(); // skip 'e' or 'E'
            if (currentChar() == '-') {
                nextChar(); // skip '+' or '-'
            }

            untilNonMatch(LexerUtil::isDigit); // skip valid digit
        }

        return number(start, cursor + 1);
    }

    /**
     * start:
     * let identifier = 1;
     *     ^
     *     |__start, cursor
     *
     * end:
     * let identifier = 1;
     *     ^        ^
     *     |        |__end, cursor
     *     |___________start
     *
     */
    private AviatorToken parseNextIdentifier() {
        int start = cursor;
        while (isIdentifierRest(peek())) {
            assertObjectAccessValid(start);
            nextChar();
        }
        assertObjectAccessEnd(start);

        return identifier(start, cursor + 1);
    }

    private void assertObjectAccessEnd(int start) {
        if (currentChar() == '.') {
            throw new AviatorLexerException(
                    format("Invalid object access syntax: <%s> expect alpha but got <%s> at %d:%d",
                            code.substring(start, cursor), toPrintable(peek()), line, column)
            );
        }
    }

    /**
     * start:
     * let identifier = /http:\/\/(.*)\.com/(.*)?(.*)/
     *                  ^
     *                  |__start, cursor
     *
     * end:
     * let identifier = /http:\/\/(.*)\.com/(.*)?(.*)/
     *                 ^                             ^
     *                 |                             |__end, cursor
     *                 |________________________________start
     */
    private AviatorToken parseNextRegexLiteral() {
        nextChar(); // skip first '/'

        int start = cursor;
        untilMeet('/', '\n', '\r');
        // /abc/
        //    ^___cursor
        // /abc<EOL>
        //    ^___cursor
        if (peek() != 0 && isNotEOL(peek())) {
            nextChar(); // cursor to '/'
        }

        assertRegexEnd(start);
        return regex(start);
    }

    /**
     * start:
     * let identifier = "abc"
     *                  ^
     *                  |__start, cursor
     *
     * end:
     * let identifier = "abc"
     *                  ^   ^
     *                  |   |__end, cursor
     *                  |______start
     */
    private AviatorToken parseNextStringLiteral() {
        char quote = currentChar();
        nextChar(); // skip start quote
        StringBuilder sb = new StringBuilder();

        int start = cursor;
        while (currentChar() != quote && isNotEOL(peek()) && currentChar() != 0) {
            if (currentChar() == '\\') {
                nextChar(); // skip '\'
                if (currentChar() == 'n') { sb.append('\n'); }
                if (currentChar() == 'r') { sb.append('\r'); }
                if (currentChar() == 't') { sb.append('\t'); }
                sb.append(currentChar());
                continue;
            }
            sb.append(currentChar());
            nextChar();
        }
        // "abc"
        //     ^___cursor
        // "abc<EOL>
        //    ^___cursor

        assertStringEnd(quote, start, cursor);
        return string(start - 1, cursor, sb.toString());
    }

    private void assertRegexEnd(int start) {
        if (currentChar() != '/') {
            throw new AviatorLexerException(
                    format("Invalid regex syntax: <%s>, expect </> but got <%s> at %d:%d",
                            code.substring(start, cursor), toPrintable(currentChar()), line, column)
            );
        }
    }

    private void assertStringEnd(char quote, int start, int end) {
        if (currentChar() != quote) {
            throw new AviatorLexerException(
                    format("Invalid string syntax: <%s>, expect <%s> but got <%s> at %d:%d",
                            code.substring(start, end), quote, toPrintable(currentChar()), line, column)
            );
        }
    }

    private AviatorToken number(int start, int end) {
        return lastToken = new AviatorToken()
                .setType(NUMBER).setLexeme(code.substring(start, end))
                .setStart(start).setEnd(end).setLine(line);
    }

    private AviatorToken identifier(int start, int end) {
        String name = code.substring(start, end);
        return lastToken = new AviatorToken()
                .setType(KEYWORDS_MAP.getOrDefault(name, IDENTIFIER)).setLexeme(name)
                .setStart(start).setEnd(end).setLine(line);
    }

    private AviatorToken regex(int start) {
        return lastToken = new AviatorToken()
                .setType(AviatorTokenType.REGEX).setLexeme(code.substring(start, cursor))
                .setStart(start).setEnd(cursor + 1).setLine(line);
    }

    private AviatorToken string(int start, int end, String string) {
        return lastToken = new AviatorToken()
                .setType(AviatorTokenType.STRING).setLexeme(string)
                .setStart(start).setEnd(end).setLine(line);
    }

    private boolean isCommentStart() {
        return currentChar() == '#' && peek() == '#';
    }


    private void assertObjectAccessValid(int start) {
        if (currentChar() == '.' && isNotIdentifierStart(peek())) {
            throw new AviatorLexerException(
                    format("Invalid object access syntax: <%s> expect alpha but got <%s> at %d:%d",
                            code.substring(start, cursor), toPrintable(peek()), line, column)
            );
        }
    }

    private AviatorToken token(AviatorTokenType type, String lexeme) {
        return lastToken = new AviatorToken()
                .setType(type).setLexeme(lexeme)
                .setStart(cursor + 1 - lexeme.length()).setEnd(cursor + 1).setLine(line);
    }

    private boolean contains(char[] chs, char ch) {
        for (char c : chs) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

    /**
     * 直到 peek 到指定字符后停止
     * 结束状态:
     * /abc.*abc/
     *          ^
     *          |__cursor
     */
    private void untilMeet(char... chs) {
        while (peek() != 0 && !contains(chs, peek())) {
            nextChar();
        }
    }

    /**
     * 直到 peek 到不匹配的字符后停止
     */
    private void untilNonMatch(Function<Character, Boolean> matcher) {
        while (peek() != 0 && matcher.apply(peek())) {
            nextChar();
        }
    }

    private char nextChar() {
        column++;
        return charAt(++cursor);
    }

    private void newLine() {
        line++;
        column = -1;
    }

    private boolean hasNext() {
        return peek() != 0;
    }

    private char currentChar() {
        return charAt(cursor);
    }

    private char peek() {
        return charAt(cursor + 1);
    }

    private char charAt(int cursor) {
        if (cursor >= code.length()) {
            return 0;
        }

        return code.charAt(cursor);
    }
}
