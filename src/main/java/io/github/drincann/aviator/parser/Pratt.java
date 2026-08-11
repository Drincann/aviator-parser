package io.github.drincann.aviator.parser;

import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ADD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.ASSIGN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_AND;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.BIT_OR;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COLON;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.COMMA;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.CONDITIONAL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.DIVIDE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.END;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.EOF;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.FALSE;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.GREATER_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.IDENTIFIER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LAMBDA;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LEFT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LESS_THAN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.LOGIC_NOT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MOD;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.MULTIPLY;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NIL;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.NUMBER;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.REGEX;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RIGHT_BRACKET;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.RIGHT_PAREN;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.STRING;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.SUBTRACT;
import static io.github.drincann.aviator.lexer.token.AviatorTokenType.TRUE;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.github.drincann.aviator.lexer.AviatorLexer;
import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.FunctionCall;
import io.github.drincann.aviator.parser.ast.LambdaFunction;
import io.github.drincann.aviator.parser.ast.LambdaParameter;
import io.github.drincann.aviator.parser.ast.Leaf;
import io.github.drincann.aviator.parser.ast.Node;
import io.github.drincann.aviator.parser.ast.OperatorReference;
import io.github.drincann.aviator.parser.ast.UnpackArgument;

/** Strict Pratt parser for AviatorScript 5.4.1 expressions. */
public final class Pratt {
    private static final Set<AviatorTokenType> LEAF_TYPES = EnumSet.of(
            NUMBER, IDENTIFIER, STRING, TRUE, FALSE, NIL, REGEX);
    private static final Set<AviatorTokenType> OPERATOR_REFERENCE_TYPES = EnumSet.of(
            ASSIGN, GREATER_THAN, LESS_THAN, ADD, SUBTRACT, MULTIPLY, DIVIDE,
            MOD, LOGIC_NOT, BIT_AND, BIT_OR);

    private final List<AviatorToken> tokens;
    private int cursor;
    private Expr result;

    public Pratt(AviatorLexer lexer) {
        this.tokens = tokenize(lexer);
    }

    public static Expr parse(String code) {
        return new Pratt(new AviatorLexer(code)).parse();
    }

    public Expr parse() {
        if (result != null) {
            return result;
        }
        if (peek(EOF)) {
            throw error("Expression must not be empty", peek());
        }
        result = expression(0);
        expect(EOF);
        return result;
    }

    private Expr expression(int minimumBindingPower) {
        Expr left = primary();
        while (true) {
            AviatorToken operator = peek();
            if (BindingPower.isInfix(operator)) {
                if (BindingPower.infixLeft(operator) < minimumBindingPower) {
                    break;
                }
                consume();
                if (operator.getType() == CONDITIONAL) {
                    left = conditional(left, operator);
                } else {
                    left = new Node(operator, left, expression(BindingPower.infixRight(operator)));
                }
                continue;
            }
            if (BindingPower.isPostfix(operator)) {
                if (BindingPower.postfix(operator) < minimumBindingPower) {
                    break;
                }
                consume();
                left = postfix(left, operator);
                continue;
            }
            break;
        }
        return left;
    }

    private Expr primary() {
        AviatorToken token = peek();
        if (token.getType() == LEFT_PAREN) {
            consume();
            Expr nested = expression(0);
            expect(RIGHT_PAREN);
            return nested;
        }
        if (LEAF_TYPES.contains(token.getType())) {
            if (token.getType() == IDENTIFIER) {
                validateIdentifier(token);
            }
            return new Leaf(consume());
        }
        if (isUnary(token.getType())) {
            consume();
            return new Node(token, expression(BindingPower.prefix(token)));
        }
        if (token.getType() == LAMBDA) {
            return lambda();
        }
        throw error("Expected an expression", token);
    }

    private Expr conditional(Expr condition, AviatorToken operator) {
        Expr thenExpression = expression(0);
        expect(COLON);
        Expr elseExpression = expression(BindingPower.infixRight(operator));
        return new Node(operator, condition, thenExpression, elseExpression);
    }

    private Expr postfix(Expr left, AviatorToken operator) {
        if (operator.getType() == LEFT_BRACKET) {
            if (peek(RIGHT_BRACKET)) {
                throw error("Array index must not be empty", peek());
            }
            Expr index = expression(0);
            expect(RIGHT_BRACKET);
            return new Node(operator, left, index);
        }
        if (operator.getType() == LEFT_PAREN) {
            return functionCall(left);
        }
        throw error("Unsupported postfix operator", operator);
    }

    private FunctionCall functionCall(Expr function) {
        List<Expr> arguments = new ArrayList<>();
        if (!peek(RIGHT_PAREN)) {
            arguments.add(functionArgument());
            while (match(COMMA)) {
                if (peek(RIGHT_PAREN)) {
                    throw error("Function call must not end with a comma", peek());
                }
                arguments.add(functionArgument());
            }
        }
        expect(RIGHT_PAREN);
        return new FunctionCall(function, arguments);
    }

    private Expr functionArgument() {
        if (isOperatorReference()) {
            return new OperatorReference(consume());
        }
        if (peek(MULTIPLY)) {
            consume();
            return new UnpackArgument(expression(0));
        }
        return expression(0);
    }

    private boolean isOperatorReference() {
        return OPERATOR_REFERENCE_TYPES.contains(peek().getType())
                && (peek(1).getType() == COMMA || peek(1).getType() == RIGHT_PAREN);
    }

    private LambdaFunction lambda() {
        expect(LAMBDA);
        expect(LEFT_PAREN);
        List<LambdaParameter> parameters = new ArrayList<>();
        if (!peek(RIGHT_PAREN)) {
            parameters.add(lambdaParameter());
            while (match(COMMA)) {
                parameters.add(lambdaParameter());
            }
        }
        expect(RIGHT_PAREN);
        validateVariadicParameter(parameters);
        expect(AviatorTokenType.ARROW);
        Expr body = expression(0);
        expect(END);
        return new LambdaFunction(parameters, body);
    }

    private LambdaParameter lambdaParameter() {
        boolean variadic = match(BIT_AND);
        AviatorToken name = expect(IDENTIFIER);
        if (!isSimpleJavaIdentifier(name.getValue()) || !name.getLexeme().equals(name.getValue())) {
            throw error("Lambda parameter must be an unquoted Java identifier", name);
        }
        return new LambdaParameter(name, variadic);
    }

    private void validateVariadicParameter(List<LambdaParameter> parameters) {
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i).isVariadic() && i != parameters.size() - 1) {
                throw error("The variadic parameter must be the last parameter",
                        parameters.get(i).getNameToken());
            }
        }
    }

    private boolean isSimpleJavaIdentifier(String value) {
        if (value.isEmpty() || !Character.isJavaIdentifierStart(value.charAt(0))) {
            return false;
        }
        for (int i = 1; i < value.length(); i++) {
            if (!Character.isJavaIdentifierPart(value.charAt(i))) {
                return false;
            }
        }
        return !"null".equals(value);
    }

    private void validateIdentifier(AviatorToken token) {
        if (token.getLexeme().startsWith("#")) {
            return;
        }
        String[] segments = token.getValue().split("\\.");
        for (String segment : segments) {
            if (!isSimpleJavaIdentifier(segment)) {
                throw error("Illegal identifier segment <" + segment + ">", token);
            }
        }
    }

    private boolean isUnary(AviatorTokenType type) {
        return type == SUBTRACT || type == LOGIC_NOT || type == BIT_NOT;
    }

    private boolean match(AviatorTokenType type) {
        if (!peek(type)) {
            return false;
        }
        consume();
        return true;
    }

    private AviatorToken expect(AviatorTokenType type) {
        AviatorToken token = peek();
        if (token.getType() != type) {
            throw error("Expected " + type, token);
        }
        return consume();
    }

    private AviatorToken consume() {
        return tokens.get(cursor++);
    }

    private AviatorToken peek() {
        return peek(0);
    }

    private AviatorToken peek(int offset) {
        int index = Math.min(cursor + offset, tokens.size() - 1);
        return tokens.get(index);
    }

    private boolean peek(AviatorTokenType type) {
        return peek().getType() == type;
    }

    private AviatorParserException error(String message, AviatorToken token) {
        return new AviatorParserException(message, token);
    }

    private static List<AviatorToken> tokenize(AviatorLexer lexer) {
        List<AviatorToken> result = new ArrayList<>();
        while (true) {
            AviatorToken token = lexer.next();
            result.add(token);
            if (token.getType() == EOF) {
                return result;
            }
        }
    }
}
