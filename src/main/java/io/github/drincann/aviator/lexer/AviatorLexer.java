package io.github.drincann.aviator.lexer;

import static io.github.drincann.aviator.lexer.token.AviatorToken.KEYWORDS_MAP;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ADD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ARROW;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ASSIGN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_AND;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_OR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_XOR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COLON;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COMMA;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONDITIONAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.END;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EOF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.FALSE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IDENTIFIER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LIKE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_AND;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_OR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MOD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MULTIPLY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NIL;
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
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.STRING;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.TRUE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.UNSIGNED_BIT_SHIFT_RIGHT;
import static io.github.drincann.aviator.util.LexerUtil.isDigit;
import static io.github.drincann.aviator.util.LexerUtil.isHexDigit;
import static io.github.drincann.aviator.util.LexerUtil.isIdentifierRest;
import static io.github.drincann.aviator.util.LexerUtil.isIdentifierStart;
import static io.github.drincann.aviator.util.LexerUtil.isStringLiteralStart;
import static io.github.drincann.aviator.util.LexerUtil.isWhiteSpace;
import static io.github.drincann.aviator.util.LexerUtil.toPrintable;

import java.util.EnumSet;
import java.util.Set;

import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;

/** Lexer for the expression subset of AviatorScript 5.4.1. */
public final class AviatorLexer {
    private static final Set<AviatorTokenType> EXPRESSION_END_TOKENS = EnumSet.of(
            NUMBER, IDENTIFIER, STRING, TRUE, FALSE, NIL, REGEX,
            RIGHT_PAREN, RIGHT_BRACKET, END);

    private final String code;
    private int cursor;
    private int line = 1;
    private AviatorToken lastToken;

    public AviatorLexer(String code) {
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        this.code = code;
        this.lastToken = new AviatorToken(EOF, "", 0, 0, 1);
    }

    public AviatorToken next() {
        skipIgnorables();
        if (isAtEnd()) {
            return new AviatorToken(EOF, "", cursor, cursor, line);
        }

        int start = cursor;
        int startLine = line;
        char ch = current();

        if (isNumberStart()) {
            return remember(number(start, startLine));
        }
        if (ch == '#') {
            return remember(quotedIdentifier(start, startLine));
        }
        int codePoint = code.codePointAt(cursor);
        if (isIdentifierStart(codePoint)) {
            return remember(identifier(start, startLine));
        }
        if (isStringLiteralStart(ch)) {
            return remember(string(start, startLine));
        }

        advance();
        switch (ch) {
            case '&':
                return remember(operator(start, startLine, consumeSeparated('&') ? LOGIC_AND : BIT_AND));
            case '|':
                return remember(operator(start, startLine, consumeSeparated('|') ? LOGIC_OR : BIT_OR));
            case '<':
                if (consumeSeparated('=')) {
                    return remember(operator(start, startLine, LESS_THAN_EQUAL));
                }
                if (consumeSeparated('<')) {
                    return remember(operator(start, startLine, SIGNED_BIT_SHIFT_LEFT));
                }
                return remember(operator(start, startLine, LESS_THAN));
            case '>':
                if (consumeSeparated('=')) {
                    return remember(operator(start, startLine, GREATER_THAN_EQUAL));
                }
                if (consumeSeparated('>')) {
                    if (consumeSeparated('>')) {
                        return remember(operator(start, startLine, UNSIGNED_BIT_SHIFT_RIGHT));
                    }
                    return remember(operator(start, startLine, SIGNED_BIT_SHIFT_RIGHT));
                }
                return remember(operator(start, startLine, GREATER_THAN));
            case '=':
                if (consumeSeparated('=')) {
                    return remember(operator(start, startLine, EQUAL));
                }
                if (consumeSeparated('~')) {
                    return remember(operator(start, startLine, LIKE));
                }
                return remember(operator(start, startLine, ASSIGN));
            case '!':
                return remember(operator(start, startLine, consumeSeparated('=') ? NOT_EQUAL : LOGIC_NOT));
            case '-':
                return remember(operator(start, startLine, consumeSeparated('>') ? ARROW : SUBTRACT));
            case '*':
                return remember(operator(start, startLine, consumeSeparated('*') ? POW : MULTIPLY));
            case '/':
                if (isDivisionContext() || isOperatorReferenceContext()) {
                    return remember(token(DIVIDE, "/", start, startLine));
                }
                cursor = start;
                line = startLine;
                return remember(regex(start, startLine));
            case '+':
                return remember(token(ADD, "+", start, startLine));
            case '%':
                return remember(token(MOD, "%", start, startLine));
            case '^':
                return remember(token(BIT_XOR, "^", start, startLine));
            case '~':
                return remember(token(BIT_NOT, "~", start, startLine));
            case '(':
                return remember(token(LEFT_PAREN, "(", start, startLine));
            case ')':
                return remember(token(RIGHT_PAREN, ")", start, startLine));
            case '[':
                return remember(token(LEFT_BRACKET, "[", start, startLine));
            case ']':
                return remember(token(RIGHT_BRACKET, "]", start, startLine));
            case '{':
                return remember(token(LEFT_BRACE, "{", start, startLine));
            case '}':
                return remember(token(RIGHT_BRACE, "}", start, startLine));
            case '.':
                return remember(token(DOT, ".", start, startLine));
            case '?':
                return remember(token(CONDITIONAL, "?", start, startLine));
            case ':':
                return remember(token(COLON, ":", start, startLine));
            case ',':
                return remember(token(COMMA, ",", start, startLine));
            case ';':
                return remember(token(SEMICOLON, ";", start, startLine));
            default:
                throw error("Unexpected character <" + toPrintable(ch) + ">", start, startLine);
        }
    }

    private AviatorToken number(int start, int startLine) {
        if (current() == '0' && (peek(1) == 'x' || peek(1) == 'X')) {
            advance();
            advance();
            // Aviator 5.4.1 always consumes the first character after 0x, even when it is not
            // a hexadecimal digit, and evaluates that digit as -1. Preserve that behavior so
            // re-serializing unusual but valid literals such as 0xG or 0x+1 is lossless.
            if (!isAtEnd()) {
                advance();
            }
            while (isHexDigit(current())) {
                advance();
            }
            return token(NUMBER, code.substring(start, cursor), start, startLine);
        }

        boolean hasDot = false;
        boolean scientificNotation = false;
        boolean exponentHasDigit = false;
        while (!isAtEnd()) {
            char ch = current();
            if (isDigit(ch)) {
                if (scientificNotation) {
                    exponentHasDigit = true;
                }
                advance();
                continue;
            }
            if (ch == '.') {
                if (hasDot || scientificNotation) {
                    throw error("Illegal number <" + code.substring(start, cursor + 1) + ">", start, startLine);
                }
                hasDot = true;
                advance();
                continue;
            }
            if (ch == 'e' || ch == 'E') {
                if (scientificNotation) {
                    throw error("Illegal number <" + code.substring(start, cursor + 1) + ">", start, startLine);
                }
                scientificNotation = true;
                advance();
                if (current() == '-') {
                    advance();
                }
                continue;
            }
            if (ch == 'M') {
                if (scientificNotation && !exponentHasDigit) {
                    throw error("Illegal number <" + code.substring(start, cursor + 1) + ">", start, startLine);
                }
                advance();
                break;
            }
            if (ch == 'N') {
                if (hasDot || scientificNotation) {
                    throw error("Big integer literal must be an integer", start, startLine);
                }
                advance();
                break;
            }
            break;
        }
        return token(NUMBER, code.substring(start, cursor), start, startLine);
    }

    private AviatorToken identifier(int start, int startLine) {
        consumeIdentifierCodePoint();
        while (!isAtEnd()) {
            int codePoint = code.codePointAt(cursor);
            if (isIdentifierRest(codePoint) || codePoint == '.') {
                advanceCodePoint(codePoint);
            } else {
                break;
            }
        }
        String lexeme = code.substring(start, cursor);
        return new AviatorToken(KEYWORDS_MAP.getOrDefault(lexeme, IDENTIFIER), lexeme, start, cursor, startLine);
    }

    private AviatorToken quotedIdentifier(int start, int startLine) {
        advance();
        if (current() == '`') {
            advance();
            int valueStart = cursor;
            while (!isAtEnd() && current() != '`') {
                advance();
            }
            if (isAtEnd()) {
                throw error("EOF while reading quoted identifier", start, startLine);
            }
            String value = code.substring(valueStart, cursor);
            advance();
            if (value.isEmpty()) {
                throw error("Blank variable name after '#'", start, startLine);
            }
            return new AviatorToken(IDENTIFIER, code.substring(start, cursor), value, start, cursor, startLine);
        }

        int valueStart = cursor;
        while (!isAtEnd()) {
            int codePoint = code.codePointAt(cursor);
            if (isIdentifierRest(codePoint) || codePoint == '.' || codePoint == '[' || codePoint == ']') {
                advanceCodePoint(codePoint);
            } else {
                break;
            }
        }
        if (valueStart == cursor) {
            throw error("Blank variable name after '#'", start, startLine);
        }
        return new AviatorToken(
                IDENTIFIER,
                code.substring(start, cursor),
                code.substring(valueStart, cursor),
                start,
                cursor,
                startLine);
    }

    private AviatorToken string(int start, int startLine) {
        char quote = current();
        advance();
        while (!isAtEnd()) {
            char ch = current();
            if (ch == quote) {
                advance();
                return token(STRING, code.substring(start, cursor), start, startLine);
            }
            if (ch == '\\') {
                advance();
                if (isAtEnd()) {
                    throw error("EOF while reading string", start, startLine);
                }
                char escaped = current();
                if (escaped != quote && "trn\\bf#".indexOf(escaped) < 0) {
                    throw error("Unsupported escape character <\\" + escaped + ">", cursor - 1, line);
                }
                advance();
                continue;
            }
            advance();
        }
        throw error("EOF while reading string", start, startLine);
    }

    private AviatorToken regex(int start, int startLine) {
        advance();
        while (!isAtEnd()) {
            char ch = current();
            if (ch == '\\') {
                advance();
                if (isAtEnd()) {
                    break;
                }
                advance();
                continue;
            }
            if (ch == '/') {
                advance();
                return token(REGEX, code.substring(start, cursor), start, startLine);
            }
            advance();
        }
        throw error("Unterminated regular expression", start, startLine);
    }

    private AviatorToken operator(int start, int startLine, AviatorTokenType type) {
        return token(type, canonicalOperator(type), start, startLine);
    }

    private String canonicalOperator(AviatorTokenType type) {
        switch (type) {
            case LOGIC_AND:
                return "&&";
            case LOGIC_OR:
                return "||";
            case LESS_THAN_EQUAL:
                return "<=";
            case SIGNED_BIT_SHIFT_LEFT:
                return "<<";
            case GREATER_THAN_EQUAL:
                return ">=";
            case SIGNED_BIT_SHIFT_RIGHT:
                return ">>";
            case UNSIGNED_BIT_SHIFT_RIGHT:
                return ">>>";
            case EQUAL:
                return "==";
            case LIKE:
                return "=~";
            case NOT_EQUAL:
                return "!=";
            case ARROW:
                return "->";
            case POW:
                return "**";
            case BIT_AND:
                return "&";
            case BIT_OR:
                return "|";
            case LESS_THAN:
                return "<";
            case GREATER_THAN:
                return ">";
            case ASSIGN:
                return "=";
            case LOGIC_NOT:
                return "!";
            case SUBTRACT:
                return "-";
            case MULTIPLY:
                return "*";
            default:
                throw new IllegalArgumentException("Not an operator token: " + type);
        }
    }

    private AviatorToken token(AviatorTokenType type, String lexeme, int start, int startLine) {
        return new AviatorToken(type, lexeme, start, cursor, startLine);
    }

    private AviatorToken remember(AviatorToken token) {
        lastToken = token;
        return token;
    }

    private boolean isNumberStart() {
        return isDigit(current())
                || current() == '.' && (isDigit(peek(1)) || peek(1) == 'e' || peek(1) == 'E');
    }

    private boolean isDivisionContext() {
        return EXPRESSION_END_TOKENS.contains(lastToken.getType());
    }

    private boolean isOperatorReferenceContext() {
        int savedCursor = cursor;
        int savedLine = line;
        skipIgnorables();
        boolean result = current() == ',' || current() == ')';
        cursor = savedCursor;
        line = savedLine;
        return result;
    }

    private boolean consumeSeparated(char expected) {
        int savedCursor = cursor;
        int savedLine = line;
        skipIgnorables();
        if (current() == expected) {
            advance();
            return true;
        }
        cursor = savedCursor;
        line = savedLine;
        return false;
    }

    private void skipIgnorables() {
        boolean skipped;
        do {
            skipped = false;
            while (!isAtEnd() && isWhiteSpace(current())) {
                advance();
                skipped = true;
            }
            if (current() == '#' && peek(1) == '#') {
                skipped = true;
                while (!isAtEnd() && current() != '\n') {
                    advance();
                }
            }
        } while (skipped);
    }

    private void consumeIdentifierCodePoint() {
        advanceCodePoint(code.codePointAt(cursor));
    }

    private void advanceCodePoint(int codePoint) {
        int count = Character.charCount(codePoint);
        for (int i = 0; i < count; i++) {
            advance();
        }
    }

    private void advance() {
        if (!isAtEnd() && code.charAt(cursor) == '\n') {
            line++;
        }
        cursor++;
    }

    private char current() {
        return peek(0);
    }

    private char peek(int offset) {
        int index = cursor + offset;
        return index >= code.length() ? 0 : code.charAt(index);
    }

    private boolean isAtEnd() {
        return cursor >= code.length();
    }

    private AviatorLexerException error(String message, int index, int errorLine) {
        return new AviatorLexerException(message + " at index " + index + ", line " + errorLine);
    }
}
