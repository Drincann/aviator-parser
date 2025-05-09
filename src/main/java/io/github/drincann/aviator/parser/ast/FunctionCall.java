package io.github.drincann.aviator.parser.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionCall implements Expr {
    private Expr function;
    private List<Expr> arguments;

    public Expr getFunction() {
        return function;
    }

    public FunctionCall setFunction(Expr function) {
        this.function = function;
        return this;
    }

    public List<Expr> getArguments() {
        return arguments;
    }

    public FunctionCall setArguments(List<Expr> arguments) {
        this.arguments = arguments;
        return this;
    }

    @Override
    public String rp() {
        return "(" + function.rp() + flatArgs() + ")";
    }

    private String flatArgs() {
        return arguments.stream().map(t -> " " + t.rp()).collect(Collectors.joining());
    }

    @Override
    public List<Expr> getChildren() {
        List<Expr> children = new ArrayList<>();
        children.add(function);
        children.addAll(arguments);
        return children;
    }

    @Override
    public String serialize() {
        return toString();
    }

    @Override
    public String toString() {
        return function + "(" + arguments.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }
}
