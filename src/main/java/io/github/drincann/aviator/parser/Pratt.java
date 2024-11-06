package io.github.drincann.aviator.parser;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COLON;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONDITIONAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EOF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.FALSE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IDENTIFIER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LAMBDA;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NIL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NUMBER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RIGHT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.STRING;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.TRUE;
import static io.github.drincann.aviator.parser.BindingPower.isInfix;
import static io.github.drincann.aviator.parser.BindingPower.isPostfix;

import java.util.ArrayList;
import java.util.List;

import io.github.drincann.aviator.lexer.AviatorLexer;
import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.FunctionCall;
import io.github.drincann.aviator.parser.ast.LambdaFunction;
import io.github.drincann.aviator.parser.ast.Leaf;
import io.github.drincann.aviator.parser.ast.Node;

public class Pratt {
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
            if (peek(EOF)) {
                break;
            }

            // a + b * c * d + e
            //             ^ ^___ peek here
            //              \____ current operator
            AviatorToken op = peek();

            if (isInfix(op)) {
                int lbp = BindingPower.infixLeft(op);
                if (lbp < ubp) {
                    break;
                }

                left = infixExpr(left, op);
                continue;
            }

            if (isPostfix(op)) {
                int rbp = BindingPower.postfix(op);
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

    private Expr primary() {
        if (peek(LEFT_PAREN)) {
            return subExpr();
        }

        if (peekLeaf()) {
            return leaf();
        }

        if (peekUnary()) {
            return prefixExpr();
        }

        if (peek(AviatorTokenType.LAMBDA)) {
            return lambda();
        }

        throw new RuntimeException("Unexpected primary token: " + token());
    }

    private Node prefixExpr() {
        next();
        AviatorToken op = token();
        Expr right = expr(BindingPower.prefix(token()));
        return new Node().setOperator(op).setOperands(right);
    }

    private Expr infixExpr(Expr left, AviatorToken op) {
        next();
        if (tokenIs(CONDITIONAL)) {
            return conditional(left, op);
        }

        return new Node().setOperator(op).setOperands(left, expr(BindingPower.infixRight(op)));
    }

    private Expr postfixExpr(Expr left, AviatorToken op) {
        next();
        if (tokenIs(LEFT_BRACKET)) {
            left = objectAccess(left, op);
        }

        if (tokenIs(LEFT_PAREN)) {
            left = functionCall(left);
        }

        return left;
    }

    private Expr subExpr() {
        eat(LEFT_PAREN);
        Expr expr = expr(0);
        eat(RIGHT_PAREN);
        return expr;
    }

    private Leaf leaf() {
        return new Leaf().setToken(next());
    }

    private LambdaFunction lambda() {
        eat(LAMBDA);
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

    private Expr conditional(Expr left, AviatorToken op) {
        Expr thenExpr = expr(0);
        eat(COLON);
        Expr elseExpr = expr(BindingPower.infixRight(op));
        left = new Node().setOperator(op).setOperands(left, thenExpr, elseExpr);
        return left;
    }

    private Expr objectAccess(Expr left, AviatorToken op) {
        Expr subexpr = expr(0);
        eat(AviatorTokenType.RIGHT_BRACKET);
        left = new Node().setOperator(op).setOperands(left, subexpr);
        return left;
    }

    private Expr functionCall(Expr left) {
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
        return left;
    }

    // utils
    private AviatorToken token() {
        return token;
    }

    private boolean tokenIs(AviatorTokenType type) {
        return token.getType().equals(type);
    }

    private boolean peekUnary() {
        return peek(SUBTRACT) || peek(LOGIC_NOT);
    }

    private boolean peekLeaf() {
        return peek(NUMBER) || peek(IDENTIFIER) || peek(STRING) || peek(TRUE) || peek(FALSE) || peek(NIL);
    }

    private AviatorToken next() {
        this.token = this.lookahead;
        this.lookahead = lexer.next();
        return this.token;
    }

    private AviatorToken peek() {
        return this.lookahead;
    }

    private boolean peek(AviatorTokenType type) {
        return peek().getType().equals(type);
    }

    private void eat(AviatorTokenType type) {
        next();
        if (!tokenIs(type)) {
            throw new RuntimeException("Expect " + type + " but got: " + token());
        }
    }
}
