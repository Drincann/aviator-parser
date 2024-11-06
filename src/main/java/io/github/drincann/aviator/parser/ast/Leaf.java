package io.github.drincann.aviator.parser.ast;

import java.util.ArrayList;
import java.util.List;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public class Leaf implements Expr {
    private AviatorToken token;

    public AviatorToken getToken() {
        return token;
    }

    public Leaf setToken(AviatorToken token) {
        this.token = token;
        return this;
    }

    @Override
    public String rp() {
        return token.getLexeme();
    }

    @Override
    public List<Expr> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return token.getLexeme();
    }
}
