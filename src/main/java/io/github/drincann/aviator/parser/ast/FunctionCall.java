package io.github.drincann.aviator.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FunctionCall implements Expr {
    private final Expr function;
    private final List<Expr> arguments;
    private final List<Expr> children;

    public FunctionCall(Expr function, List<Expr> arguments) {
        this.function = Objects.requireNonNull(function, "function");
        this.arguments = Collections.unmodifiableList(new ArrayList<>(arguments));
        List<Expr> allChildren = new ArrayList<>(arguments.size() + 1);
        allChildren.add(function);
        allChildren.addAll(arguments);
        this.children = Collections.unmodifiableList(allChildren);
    }

    public Expr getFunction() {
        return function;
    }

    public List<Expr> getArguments() {
        return arguments;
    }

    @Override
    public String rp() {
        return "(" + function.rp()
                + arguments.stream().map(argument -> " " + argument.rp()).collect(Collectors.joining())
                + ")";
    }

    @Override
    public List<Expr> getChildren() {
        return children;
    }

    @Override
    public String serialize() {
        return function.serialize() + "("
                + arguments.stream().map(Expr::serialize).collect(Collectors.joining(", "))
                + ")";
    }

    @Override
    public String toString() {
        return serialize();
    }
}
