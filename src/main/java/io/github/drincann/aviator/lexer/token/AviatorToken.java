package io.github.drincann.aviator.lexer.token;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BREAK;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CATCH;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONTINUE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ELSE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ELSE_IF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.END;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.FALSE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.FINALLY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.FN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.FOR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LAMBDA;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NEW;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NIL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RETURN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.THROW;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.TRUE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.TRY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.USE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.WHILE;
import static io.github.drincann.aviator.util.CollectionUtil.map;

import java.util.Map;
import java.util.Objects;

/** An immutable lexical token with both its source spelling and semantic value. */
public final class AviatorToken {
    public static final Map<String, AviatorTokenType> KEYWORDS_MAP =
            map(String.class, AviatorTokenType.class,
                    "if", IF, "else", ELSE, "elsif", ELSE_IF,
                    "for", FOR, "in", IN, "while", WHILE,
                    "break", BREAK, "continue", CONTINUE, "return", RETURN,
                    "try", TRY, "catch", CATCH, "finally", FINALLY, "throw", THROW,
                    "fn", FN, "lambda", LAMBDA, "end", END,
                    "true", TRUE, "false", FALSE, "nil", NIL,
                    "let", LET, "new", NEW, "use", USE
            );

    private final AviatorTokenType type;
    private final String lexeme;
    private final String value;
    private final int start;
    private final int end;
    private final int line;

    public AviatorToken(AviatorTokenType type, String lexeme, int start, int end, int line) {
        this(type, lexeme, lexeme, start, end, line);
    }

    public AviatorToken(
            AviatorTokenType type,
            String lexeme,
            String value,
            int start,
            int end,
            int line) {
        this.type = Objects.requireNonNull(type, "type");
        this.lexeme = Objects.requireNonNull(lexeme, "lexeme");
        this.value = Objects.requireNonNull(value, "value");
        this.start = start;
        this.end = end;
        this.line = line;
    }

    public AviatorTokenType getType() {
        return type;
    }

    /**
     * Returns the exact source spelling, except that whitespace-separated operators are canonicalized.
     *
     * @return token spelling used for lossless expression serialization
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * Returns the semantic value. It differs from lexeme for quoted identifiers.
     *
     * @return value used for identifier lookup and AST analysis
     */
    public String getValue() {
        return value;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "AviatorToken{"
                + "type=" + type
                + ", lexeme='" + lexeme + '\''
                + ", value='" + value + '\''
                + ", start=" + start
                + ", end=" + end
                + ", line=" + line
                + '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AviatorToken)) {
            return false;
        }
        AviatorToken that = (AviatorToken) other;
        return start == that.start
                && end == that.end
                && line == that.line
                && type == that.type
                && lexeme.equals(that.lexeme)
                && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme, value, start, end, line);
    }
}
