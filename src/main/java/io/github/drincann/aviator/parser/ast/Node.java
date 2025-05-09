package io.github.drincann.aviator.parser.ast;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public class Node implements Expr {
    private AviatorToken operator;
    private List<Expr> operands;
    private List<Expr> children;


    private String flat(List<Expr> exprs) {
        return exprs.stream().map(t -> " " + t.rp()).collect(Collectors.joining());
    }

    public AviatorToken getOperator() {
        return operator;
    }

    public Node setOperator(AviatorToken operator) {
        this.operator = operator;
        return this;
    }

    public List<Expr> getOperands() {
        return operands;
    }

    public Node setOperands(Expr... operands) {
        this.operands = new ArrayList<>();
        Collections.addAll(this.operands, operands);
        this.children = new ArrayList<>();
        Collections.addAll(this.children, operands);
        return this;
    }

    @Override
    public String rp() {
        return '('
                + operator.getLexeme()
                + flat(operands)
                + ')';
    }

    @Override
    public List<Expr> getChildren() {
        return children;
    }

    @Override
    public String serialize() {
        return toString();
    }

    @Override
    public String toString() {
        if (operands.size() == 1) {
            return "(" + operator.getLexeme() + operands.get(0) + ")";
        }

        if (operands.size() == 2) {
            if (operator.getType().equals(DOT)) {
                return operands.get(0) + operator.getLexeme() + operands.get(1);
            }
            if (operator.getType().equals(LEFT_BRACKET)) {
                return operands.get(0) + "[" + operands.get(1) + "]";
            }
            return "(" + operands.get(0) + " " + operator.getLexeme() + " " + operands.get(1) + ")";
        }

        if (operands.size() == 3) {
            return "(" + operands.get(0) + " " + operator.getLexeme() + " " + operands.get(1) + ":" + operands.get(2) + ")";
        }

        throw new RuntimeException("Unexpected node: " + operator.getLexeme() + " " + operands);
    }
}
