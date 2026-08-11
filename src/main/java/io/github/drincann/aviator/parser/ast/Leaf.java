package io.github.drincann.aviator.parser.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public final class Leaf implements Expr {
    private final AviatorToken token;

    public Leaf(AviatorToken token) {
        this.token = Objects.requireNonNull(token, "token");
    }

    public AviatorToken getToken() {
        return token;
    }

    @Override
    public String rp() {
        return token.getLexeme();
    }

    @Override
    public List<Expr> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String serialize() {
        return token.getLexeme();
    }

    @Override
    public String toString() {
        return serialize();
    }
}
