package io.github.drincann.aviator.parser.ast;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public final class LambdaFunction implements Expr {
    private final List<LambdaParameter> parameters;
    private final Expr body;

    public LambdaFunction(List<LambdaParameter> parameters, Expr body) {
        this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
        this.body = Objects.requireNonNull(body, "body");
    }

    public List<LambdaParameter> getParameters() {
        return parameters;
    }

    public Expr getBody() {
        return body;
    }

    @Override
    public String rp() {
        return "lambda (" + parameters.stream().map(LambdaParameter::serialize).collect(Collectors.joining(" "))
                + ") -> " + body.rp() + " end";
    }

    @Override
    public List<Expr> getChildren() {
        return Collections.singletonList(body);
    }

    @Override
    public String serialize() {
        return "lambda ("
                + parameters.stream().map(LambdaParameter::serialize).collect(Collectors.joining(", "))
                + ") -> " + body.serialize() + " end";
    }

    @Override
    public String toString() {
        return serialize();
    }
}
