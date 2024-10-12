package io.github.drincann.aviator.lexer.token;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ARROW;
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

public class AviatorToken {
    public static final Map<String, AviatorTokenType> KEYWORDS_MAP =
            map(String.class, AviatorTokenType.class,
                    "if", IF, "else", ELSE, "elsif", ELSE_IF,

                    "for", FOR, "in", IN, "while", WHILE,
                    "break", BREAK, "continue", CONTINUE, "return", RETURN,

                    "try", TRY, "catch", CATCH, "finally", FINALLY, "throw", THROW,

                    "fn", FN, "lambda", LAMBDA, "->", ARROW, "end", END,

                    "true", TRUE, "false", FALSE, "nil", NIL,

                    "let", LET, "new", NEW, "use", USE
            );

    private AviatorTokenType type;
    private String lexeme;

    // meta
    private Integer start; // token 开始的第一个字符的位置
    private Integer end; // token 结束的下一个字符的位置
    private Integer line; // token 所在行号（目前 token 不会跨行）

    public AviatorTokenType getType() {
        return type;
    }

    public AviatorToken setType(AviatorTokenType type) {
        this.type = type;
        return this;
    }

    public String getLexeme() {
        return lexeme;
    }

    public AviatorToken setLexeme(String lexeme) {
        this.lexeme = lexeme;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public AviatorToken setStart(Integer start) {
        this.start = start;
        return this;
    }

    public Integer getEnd() {
        return end;
    }

    public AviatorToken setEnd(Integer end) {
        this.end = end;
        return this;
    }

    public Integer getLine() {
        return line;
    }

    public AviatorToken setLine(Integer line) {
        this.line = line;
        return this;
    }

    @Override
    public String toString() {
        return "AviatorToken{"
                + "type=" + type
                + ", lexeme='" + lexeme + '\''
                + ", start=" + start
                + ", end=" + end
                + ", line=" + line
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AviatorToken that = (AviatorToken) o;
        return type == that.type && Objects.equals(lexeme, that.lexeme) && Objects.equals(start, that.start)
                && Objects.equals(end, that.end) && Objects.equals(line, that.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme, start, end, line);
    }
}