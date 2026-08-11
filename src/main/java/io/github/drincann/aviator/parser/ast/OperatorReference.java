package io.github.drincann.aviator.parser.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.drincann.aviator.lexer.token.AviatorToken;

/** A binary operator used as a first-class function argument, for example reduce(xs, +, 0). */
public final class OperatorReference implements Expr {
    private final AviatorToken operator;

    public OperatorReference(AviatorToken operator) {
        this.operator = Objects.requireNonNull(operator, "operator");
    }

    public AviatorToken getOperator() {
        return operator;
    }

    @Override
    public List<Expr> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String rp() {
        return operator.getLexeme();
    }

    @Override
    public String serialize() {
        return operator.getLexeme();
    }

    @Override
    public String toString() {
        return serialize();
    }
}
