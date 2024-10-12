package io.github.drincann.aviator.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.github.drincann.aviator.lexer.token.AviatorToken;

public class Node implements Expr {

    private AviatorToken operator;
    private List<Expr> operands;

    public String rp() {
        return '('
                + operator.getLexeme()
                + flat(operands)
                + ')';
    }

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
        return this;
    }

    @Override
    public String toString() {
        return operator.getLexeme() + flat(operands);
    }
}
