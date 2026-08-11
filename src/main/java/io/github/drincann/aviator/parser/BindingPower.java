package io.github.drincann.aviator.parser;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ADD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ASSIGN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_AND;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_OR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_XOR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONDITIONAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN_EQUAL;
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
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NOT_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.POW;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SIGNED_BIT_SHIFT_LEFT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SIGNED_BIT_SHIFT_RIGHT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.UNSIGNED_BIT_SHIFT_RIGHT;

import java.util.Map;

import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.util.CollectionUtil;

/** AviatorScript 5.4.1 binding powers. */
final class BindingPower {
    private static final Map<AviatorTokenType, Integer> INFIX_LEFT =
            CollectionUtil.map(AviatorTokenType.class, Integer.class,
                    CONDITIONAL, 2,
                    LOGIC_OR, 3,
                    LOGIC_AND, 5,
                    BIT_OR, 7,
                    BIT_XOR, 9,
                    BIT_AND, 11,
                    ASSIGN, 13, LIKE, 13, EQUAL, 13, NOT_EQUAL, 13,
                    GREATER_THAN, 15, GREATER_THAN_EQUAL, 15, LESS_THAN, 15, LESS_THAN_EQUAL, 15,
                    SIGNED_BIT_SHIFT_LEFT, 17, SIGNED_BIT_SHIFT_RIGHT, 17, UNSIGNED_BIT_SHIFT_RIGHT, 17,
                    ADD, 19, SUBTRACT, 19,
                    MULTIPLY, 21, DIVIDE, 21, MOD, 21,
                    POW, 25
            );

    private static final Map<AviatorTokenType, Integer> INFIX_RIGHT =
            CollectionUtil.map(AviatorTokenType.class, Integer.class,
                    CONDITIONAL, 1,
                    LOGIC_OR, 4,
                    LOGIC_AND, 6,
                    BIT_OR, 8,
                    BIT_XOR, 10,
                    BIT_AND, 12,
                    ASSIGN, 0, LIKE, 14, EQUAL, 14, NOT_EQUAL, 14,
                    GREATER_THAN, 16, GREATER_THAN_EQUAL, 16, LESS_THAN, 16, LESS_THAN_EQUAL, 16,
                    SIGNED_BIT_SHIFT_LEFT, 18, SIGNED_BIT_SHIFT_RIGHT, 18, UNSIGNED_BIT_SHIFT_RIGHT, 18,
                    ADD, 20, SUBTRACT, 20,
                    MULTIPLY, 22, DIVIDE, 22, MOD, 22,
                    // Exponentiation is right associative and accepts a unary expression on its right.
                    POW, 23
            );

    private static final Map<AviatorTokenType, Integer> PREFIX =
            CollectionUtil.map(AviatorTokenType.class, Integer.class,
                    SUBTRACT, 23,
                    LOGIC_NOT, 23,
                    BIT_NOT, 23
            );

    private static final Map<AviatorTokenType, Integer> POSTFIX =
            CollectionUtil.map(AviatorTokenType.class, Integer.class,
                    LEFT_PAREN, 29,
                    LEFT_BRACKET, 29
            );

    private BindingPower() {
    }

    static boolean isPostfix(AviatorToken token) {
        return POSTFIX.containsKey(token.getType());
    }

    static boolean isInfix(AviatorToken token) {
        return INFIX_LEFT.containsKey(token.getType());
    }

    static int infixRight(AviatorToken token) {
        return INFIX_RIGHT.get(token.getType());
    }

    static int infixLeft(AviatorToken token) {
        return INFIX_LEFT.get(token.getType());
    }

    static int prefix(AviatorToken token) {
        return PREFIX.get(token.getType());
    }

    static int postfix(AviatorToken token) {
        return POSTFIX.get(token.getType());
    }
}
