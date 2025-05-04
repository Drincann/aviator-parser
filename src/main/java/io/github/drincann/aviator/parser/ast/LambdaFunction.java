package io.github.drincann.aviator.parser.ast;

import java.util.ArrayList;
import java.util.List;

public class LambdaFunction implements Expr {
    private List<String> parameters;
    private Expr body;

    public List<String> getParameters() {
        return parameters;
    }

    public LambdaFunction setParameters(List<String> parameters) {
        this.parameters = parameters;
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
        return "lambda (" + String.join(" ", parameters) + ") -> " + body.rp() + " end";
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
        return "lambda (" + String.join(", ", parameters) + ") -> " + body + " end";
    }
}
