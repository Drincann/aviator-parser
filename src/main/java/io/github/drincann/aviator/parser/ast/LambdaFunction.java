package io.github.drincann.aviator.parser.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public class LambdaFunction implements Expr {
    private List<Leaf> parameters;
    private Expr body;

    public List<Leaf> getParameters() {
        return parameters;
    }

    public LambdaFunction setParameters(List<AviatorToken> parameters) {
        this.parameters = parameters.stream().map(token -> new Leaf().setToken(token)).collect(Collectors.toList());
        return this;
    }

    public Expr getBody() {
        return body;
    }

    public LambdaFunction setBody(Expr body) {
        this.body = body;
        return this;
    }

    @Override
    public String rp() {
        return "lambda (" + String.join(" ", toStringList(parameters)) + ") -> " + body.rp() + " end";
    }

    private List<String> toStringList(List<Leaf> parameters) {
        return parameters.stream().map(Leaf::toString).collect(Collectors.toList());
    }

    @Override
    public List<Expr> getChildren() {
        return new ArrayList<Expr>() {{ add(body); }};
    }

    @Override
    public String serialize() {
        return toString();
    }

    @Override
    public String toString() {
        return "lambda (" + String.join(", ", toStringList(parameters)) + ") -> " + body + " end";
    }
}
