package io.github.drincann.aviator.parser;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ADD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONDITIONAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_AND;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_OR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MULTIPLY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NOT_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;

import java.util.Map;

import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.util.CollectionUtil;

/**
 * Binding power config of operators
 */
public class BindingPower {
    static final Map<AviatorTokenType, Integer> INFIX_OPERATOR_LEFT_BINDING_POWER =
            CollectionUtil.map(AviatorTokenType.class, Integer.class,
                    CONDITIONAL, 2,
                    LOGIC_OR, 3,
                    LOGIC_AND, 9,
                    EQUAL, 7, NOT_EQUAL, 7,
                    GREATER_THAN, 9, GREATER_THAN_EQUAL, 9, LESS_THAN, 9, LESS_THAN_EQUAL, 9,
                    ADD, 11, SUBTRACT, 11,
                    MULTIPLY, 13, DIVIDE, 13,
                    DOT, 15
            );
    static final Map<AviatorTokenType, Integer> INFIX_OPERATOR_RIGHT_BINDING_POWER =
            CollectionUtil.map(AviatorTokenType.class, Integer.class,
                    CONDITIONAL, 1,
                    LOGIC_OR, 4,
                    LOGIC_AND, 6,
                    EQUAL, 8, NOT_EQUAL, 8,
                    GREATER_THAN, 10, GREATER_THAN_EQUAL, 10, LESS_THAN, 10, LESS_THAN_EQUAL, 10,
                    ADD, 12, SUBTRACT, 12,
                    MULTIPLY, 14, DIVIDE, 14,
                    DOT, 16
            );
    static final Map<AviatorTokenType, Integer> PREFIX_OPERATOR_BINDING_POWER =
            CollectionUtil.map(AviatorTokenType.class, Integer.class,
                    SUBTRACT, 15,
                    LOGIC_NOT, 15
            );
    static final Map<AviatorTokenType, Integer> POSTFIX_OPERATOR_BINDING_POWER =
            CollectionUtil.map(AviatorTokenType.class, Integer.class,
                    LEFT_PAREN, 15, LEFT_BRACKET, 15
            );

    static boolean isPostfix(AviatorToken op) {
        return POSTFIX_OPERATOR_BINDING_POWER.containsKey(op.getType());
    }

    static boolean isInfix(AviatorToken op) {
        return INFIX_OPERATOR_LEFT_BINDING_POWER.containsKey(op.getType());
    }

    static int infixRight(AviatorToken op) {
        return INFIX_OPERATOR_RIGHT_BINDING_POWER.get(op.getType());
    }

    static int infixLeft(AviatorToken op) {
        return INFIX_OPERATOR_LEFT_BINDING_POWER.get(op.getType());
    }

    static int prefix(AviatorToken op) {
        return PREFIX_OPERATOR_BINDING_POWER.get(op.getType());
    }

    static Integer postfix(AviatorToken op) {
        return POSTFIX_OPERATOR_BINDING_POWER.get(op.getType());
    }
}