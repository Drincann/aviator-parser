package io.github.drincann.aviator.parser;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ADD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COLON;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONDITIONAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EOF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.FALSE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IDENTIFIER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_AND;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_OR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MULTIPLY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NIL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NOT_EQUAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NUMBER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RIGHT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.STRING;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.TRUE;
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
            LOGIC_OR, 3,
            LOGIC_AND, 9,
            EQUAL, 7, NOT_EQUAL, 7,
            GREATER_THAN, 9, GREATER_THAN_EQUAL, 9, LESS_THAN, 9, LESS_THAN_EQUAL, 9,
            ADD, 11, SUBTRACT, 11,
            MULTIPLY, 13, DIVIDE, 13,
            DOT, 15
    );

    private static final Map<AviatorTokenType, Integer> INFIX_OPERATOR_RIGHT_BINDING_POWER = map(AviatorTokenType.class, Integer.class,
            CONDITIONAL, 1,
            LOGIC_OR, 4,
            LOGIC_AND, 6,
            EQUAL, 8, NOT_EQUAL, 8,
            GREATER_THAN, 10, GREATER_THAN_EQUAL, 10, LESS_THAN, 10, LESS_THAN_EQUAL, 10,
            ADD, 12, SUBTRACT, 12,
            MULTIPLY, 14, DIVIDE, 14,
            DOT, 16
    );

    private static final Map<AviatorTokenType, Integer> PREFIX_OPERATOR_BINDING_POWER = map(AviatorTokenType.class, Integer.class,
            SUBTRACT, 15,
            LOGIC_NOT, 15
    );

    private static final Map<AviatorTokenType, Integer> POSTFIX_OPERATOR_BINDING_POWER = map(AviatorTokenType.class, Integer.class,
            LEFT_PAREN, 15, LEFT_BRACKET, 15
    );

    private final AviatorLexer lexer;

    private AviatorToken token;

    private AviatorToken lookahead;

    private Expr result;

    public Pratt(AviatorLexer lexer) {
        this.lexer = lexer;
    }

    public static Expr parse(String code) {
        return new Pratt(new AviatorLexer(code)).parse();
    }

    private Expr parse() {
        if (this.result != null) {
            return result;
        }
        next();
        this.result = expr(0);
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

        if (tokenIs(LEFT_PAREN)) {
            // function call
            List<Expr> arguments = new ArrayList<>();
            while (!peek(RIGHT_PAREN)) {
                arguments.add(expr(0));
                if (peek(RIGHT_PAREN)) {
                    break;
                }
                eat(AviatorTokenType.COMMA);
            }
            left = new FunctionCall().setFunction(left).setArguments(arguments);
            eat(RIGHT_PAREN);
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

        // lambda function
        // lambda (...params) -> body end
        if (tokenIs(AviatorTokenType.LAMBDA)) {
            eat(LEFT_PAREN);
            List<String> parameters = new ArrayList<>();
            while (!tokenIs(RIGHT_PAREN)) {
                parameters.add(next().getLexeme());
                if (peek(RIGHT_PAREN)) {
                    break;
                }
                eat(AviatorTokenType.COMMA);
            }
            eat(RIGHT_PAREN);
            eat(AviatorTokenType.ARROW);
            Expr body = expr(0);
            eat(AviatorTokenType.END);
            return new LambdaFunction().setParameters(parameters).setBody(body);
        }

        throw new RuntimeException("Unexpected primary token: " + token());
    }

    private boolean tokenIsUnary() {
        return tokenIs(SUBTRACT) || tokenIs(LOGIC_NOT);

    }

    private boolean tokenIsLeaf() {
        return tokenIs(NUMBER) || tokenIs(IDENTIFIER) || tokenIs(STRING) || tokenIs(TRUE) || tokenIs(FALSE) || tokenIs(NIL);
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

    private boolean peekEof() {
        return peek().getType().equals(EOF);
    }

    private boolean peek(AviatorTokenType type) {
        return peek().getType().equals(type);
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

    public interface Expr {
        String rp();

        List<Expr> getChildren();
    }

    public static class Leaf implements Expr {
        private AviatorToken token;

        public String rp() {
            return token.getLexeme();
        }

        @Override
        public List<Expr> getChildren() {
            return new ArrayList<>();
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
        private List<Expr> children;

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

        public List<Expr> getChildren() {
            return children;
        }

        public Node setOperands(Expr... operands) {
            this.operands = new ArrayList<>();
            Collections.addAll(this.operands, operands);
            this.children = new ArrayList<>();
            Collections.addAll(this.children, operands);
            return this;
        }

        @Override
        public String toString() {
            if (operands.size() == 1) {
                return "(" + operator.getLexeme() + operands.get(0) + ")";
            }

            if (operands.size() == 2) {
                return "(" + operands.get(0) + " " + operator.getLexeme() + " " + operands.get(1) + ")";
            }

            if (operands.size() == 3) {
                return "(" + operands.get(0) + " " + operator.getLexeme() + " " + operands.get(1) + ":" + operands.get(2) + ")";
            }

            throw new RuntimeException("Unexpected node: " + operator.getLexeme() + " " + operands);
        }
    }

    public static class FunctionCall implements Expr {
        private Expr function;
        private List<Expr> arguments;

        public FunctionCall setFunction(Expr function) {
            this.function = function;
            return this;
        }

        public FunctionCall setArguments(List<Expr> arguments) {
            this.arguments = arguments;
            return this;
        }

        public String rp() {
            return "(" + function.rp() + flat(arguments) + ")";
        }

        @Override
        public List<Expr> getChildren() {
            return new ArrayList<>(arguments);
        }

        private String flat(List<Expr> exprs) {
            return exprs.stream().map(t -> " " + t.rp()).collect(Collectors.joining());
        }

        @Override
        public String toString() {
            return function + "(" + arguments.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
        }
    }

    public static class LambdaFunction implements Expr {
        private List<String> parameters;
        private Expr body;

        public LambdaFunction setParameters(List<String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public LambdaFunction setBody(Expr body) {
            this.body = body;
            return this;
        }

        public String rp() {
            return "lambda (" + String.join(" ", parameters) + ") -> " + body.rp() + " end";
        }

        @Override
        public List<Expr> getChildren() {
            return new ArrayList<Expr>() {{ add(body); }};
        }

        @Override
        public String toString() {
            return "lambda (" + String.join(", ", parameters) + ") -> " + body + " end";
        }
    }
}
