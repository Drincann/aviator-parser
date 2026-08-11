package io.github.drincann.aviator.parser.ast;

import java.util.Objects;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public final class LambdaParameter {
    private final AviatorToken nameToken;
    private final boolean variadic;

    public LambdaParameter(AviatorToken nameToken, boolean variadic) {
        this.nameToken = Objects.requireNonNull(nameToken, "nameToken");
        this.variadic = variadic;
    }

    public AviatorToken getNameToken() {
        return nameToken;
    }

    public String getName() {
        return nameToken.getValue();
    }

    public boolean isVariadic() {
        return variadic;
    }

    public String serialize() {
        return (variadic ? "&" : "") + nameToken.getLexeme();
    }

    @Override
    public String toString() {
        return serialize();
    }
}
