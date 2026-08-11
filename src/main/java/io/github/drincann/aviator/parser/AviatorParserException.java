package io.github.drincann.aviator.parser;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public final class AviatorParserException extends RuntimeException {
    private final AviatorToken token;

    public AviatorParserException(String message, AviatorToken token) {
        super(message + " at index " + token.getStart() + ", line " + token.getLine()
                + ", token " + token.getType() + " <" + token.getLexeme() + ">");
        this.token = token;
    }

    public AviatorToken getToken() {
        return token;
    }
}
