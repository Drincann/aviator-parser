package io.github.drincann.aviator.parser.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** A function argument expanded with Aviator's *argument syntax. */
public final class UnpackArgument implements Expr {
    private final Expr expression;

    public UnpackArgument(Expr expression) {
        this.expression = Objects.requireNonNull(expression, "expression");
    }

    public Expr getExpression() {
        return expression;
    }

    @Override
    public List<Expr> getChildren() {
        return Collections.singletonList(expression);
    }

    @Override
    public String rp() {
        return "(* " + expression.rp() + ")";
    }

    @Override
    public String serialize() {
        return "*" + expression.serialize();
    }

    @Override
    public String toString() {
        return serialize();
    }
}
