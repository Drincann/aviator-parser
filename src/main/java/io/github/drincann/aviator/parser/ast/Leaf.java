package io.github.drincann.aviator.parser.ast;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public class Leaf implements Expr {
    private AviatorToken token;

    public String rp() {
        return token.getLexeme();
    }

    public AviatorToken getToken() {
        return token;
    }

    public Leaf setToken(AviatorToken token) {
        this.token = token;
        return this;
    }

    @Override
    public String toString() {
        return token.getLexeme();
    }
}
