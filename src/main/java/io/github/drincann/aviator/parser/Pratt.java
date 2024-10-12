package io.github.drincann.aviator.parser;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ADD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COLON;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONDITIONAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EOF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IDENTIFIER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MULTIPLY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NUMBER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RIGHT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.STRING;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;
import static io.github.drincann.aviator.util.CollectionUtil.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.drincann.aviator.lexer.AviatorLexer;
import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;


public class Pratt {
    private static final Map<AviatorTokenType, Integer> INFIX_OPERATOR_LEFT_BINDING_POWER = map(AviatorTokenType.class, Integer.class,
            CONDITIONAL, 2,
            ADD, 3, SUBTRACT, 3,
            MULTIPLY, 5, DIVIDE, 5,
            DOT, 7
    );

    private static final Map<AviatorTokenType, Integer> INFIX_OPERATOR_RIGHT_BINDING_POWER = map(AviatorTokenType.class, Integer.class,
            CONDITIONAL, 1,
            ADD, 4, SUBTRACT, 4,
            MULTIPLY, 6, DIVIDE, 6,
            DOT, 8
    );

    private static final Map<AviatorTokenType, Integer> PREFIX_OPERATOR_BINDING_POWER = map(AviatorTokenType.class, Integer.class,
            SUBTRACT, 7
    );

    private static final Map<AviatorTokenType, Integer> POSTFIX_OPERATOR_BINDING_POWER = map(AviatorTokenType.class, Integer.class,
            LEFT_PAREN, 7, LEFT_BRACKET, 7
    );

    private final AviatorLexer lexer;

    private AviatorToken token;

    private AviatorToken lookahead;

    private String result;

    public Pratt(AviatorLexer lexer) {
        this.lexer = lexer;
    }

    public static Pratt parse(String code) {
        return new Pratt(new AviatorLexer(code));
    }

    public String rp() {
        if (this.result != null) {
            return result;
        }
        next();
        this.result = expr(0).rp();
        return this.result;
    }

    private Expr expr(int ubp) {
        Expr left = primary();

        while (true) {
            if (peekEof()) {
                break;
            }

            // a + b * c * d + e
            //             ^ ^___ peek here
            //              \____ current operator
            AviatorToken op = peek();

            if (isInfix(op)) {
                int lbp = infixLbp(op);
                if (lbp < ubp) {
                    break;
                }

                left = infixExpr(left, op);
                continue;
            }

            if (isPostfix(op)) {
                int rbp = postfixBp(op);
                if (rbp < ubp) {
                    break;
                }

                left = postfixExpr(left, op);
                continue;
            }

            break;
        }

        return left;
    }

    private Expr infixExpr(Expr left, AviatorToken op) {
        next(); // skip left
        if (is(op, CONDITIONAL)) {
            Expr thenExpr = expr(0);
            eat(COLON);
            Expr elseExpr = expr(infixRbp(op));
            left = new Node().setOperator(op).setOperands(left, thenExpr, elseExpr);
            return left;
        }

        return new Node().setOperator(op).setOperands(left, expr(infixRbp(op)));
    }

    private boolean is(AviatorToken token, AviatorTokenType type) {
        return token.getType().equals(type);
    }

    private Expr postfixExpr(Expr left, AviatorToken op) {
        next(); // skip left
        if (tokenIs(LEFT_BRACKET)) {
            // object/array access
            Expr subexpr = expr(0);
            eat(AviatorTokenType.RIGHT_BRACKET);
            left = new Node().setOperator(op).setOperands(left, subexpr);
        }

        return left;
    }

    private boolean isPostfix(AviatorToken op) {
        return POSTFIX_OPERATOR_BINDING_POWER.containsKey(op.getType());
    }

    private boolean isInfix(AviatorToken op) {
        return INFIX_OPERATOR_LEFT_BINDING_POWER.containsKey(op.getType());
    }

    private Expr primary() {
        next();
        if (tokenIs(LEFT_PAREN)) {
            Expr expr = expr(0);
            eat(RIGHT_PAREN);
            return expr;
        }

        if (tokenIsLeaf()) {
            return new Leaf().setToken(token());
        }

        if (tokenIsUnary()) {
            AviatorToken op = token();
            Expr right = expr(prefixBp(token()));
            return new Node().setOperator(op).setOperands(right);
        }

        throw new RuntimeException("Unexpected primary token: " + token());
    }

    private boolean tokenIsUnary() {
        return tokenIs(SUBTRACT);

    }

    private boolean tokenIsLeaf() {
        return tokenIs(NUMBER) || tokenIs(IDENTIFIER) || tokenIs(STRING);
    }

    private boolean peekEof() {
        return peek().getType().equals(EOF);
    }

    private static int infixRbp(AviatorToken op) {
        return INFIX_OPERATOR_RIGHT_BINDING_POWER.get(op.getType());
    }

    private static int infixLbp(AviatorToken op) {
        return INFIX_OPERATOR_LEFT_BINDING_POWER.get(op.getType());
    }


    private int prefixBp(AviatorToken op) {
        Integer bp = PREFIX_OPERATOR_BINDING_POWER.get(op.getType());
        if (bp == null) {
            throw new RuntimeException("Unexpected prefix operator: " + op);
        }
        return bp;
    }

    private static Integer postfixBp(AviatorToken op) {
        return POSTFIX_OPERATOR_BINDING_POWER.get(op.getType());
    }


    private AviatorToken token() {
        return token;
    }

    private boolean tokenIs(AviatorTokenType type) {
        return token.getType().equals(type);
    }

    private AviatorToken next() {
        this.token = this.lookahead;
        this.lookahead = lexer.next();
        return this.token;
    }

    private AviatorToken peek() {
        return this.lookahead;
    }

    private void eat(AviatorTokenType type) {
        next();
        if (!tokenIs(type)) {
            throw new RuntimeException("Expect " + type + " but got: " + token());
        }
    }

    public static interface Expr {
        String rp();
    }

    public static class Leaf implements Expr {
        private AviatorToken token;

        public String rp() {
            return token.getLexeme();
        }

        public AviatorToken getToken() {
            return token;
        }

        public Leaf setToken(AviatorToken token) {
            this.token = token;
            return this;
        }

        @Override
        public String toString() {
            return token.getLexeme();
        }
    }

    public static class Node implements Expr {

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
}
