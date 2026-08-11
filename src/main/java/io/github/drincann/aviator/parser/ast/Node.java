package io.github.drincann.aviator.parser.ast;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public final class Node implements Expr {
    private final AviatorToken operator;
    private final List<Expr> operands;

    public Node(AviatorToken operator, Expr... operands) {
        this.operator = Objects.requireNonNull(operator, "operator");
        this.operands = Collections.unmodifiableList(Arrays.asList(operands.clone()));
    }

    public AviatorToken getOperator() {
        return operator;
    }

    public List<Expr> getOperands() {
        return operands;
    }

    @Override
    public String rp() {
        return '(' + operator.getLexeme()
                + operands.stream().map(operand -> " " + operand.rp()).collect(Collectors.joining())
                + ')';
    }

    @Override
    public List<Expr> getChildren() {
        return operands;
    }

    @Override
    public String serialize() {
        if (operands.size() == 1) {
            return "(" + operator.getLexeme() + operands.get(0).serialize() + ")";
        }
        if (operands.size() == 2) {
            if (operator.getType() == LEFT_BRACKET) {
                return operands.get(0).serialize() + "[" + operands.get(1).serialize() + "]";
            }
            return "(" + operands.get(0).serialize() + " " + operator.getLexeme() + " "
                    + operands.get(1).serialize() + ")";
        }
        if (operands.size() == 3) {
            return "(" + operands.get(0).serialize() + " ? " + operands.get(1).serialize() + " : "
                    + operands.get(2).serialize() + ")";
        }
        throw new IllegalStateException("Unexpected operand count for " + operator.getLexeme() + ": " + operands.size());
    }

    @Override
    public String toString() {
        return serialize();
    }
}
