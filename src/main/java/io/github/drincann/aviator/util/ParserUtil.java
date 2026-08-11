package io.github.drincann.aviator.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.parser.Pratt;
import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.Leaf;
import io.github.drincann.aviator.parser.ast.Node;

public final class ParserUtil {
    private ParserUtil() {
    }

    public static List<Expr> getLeafParentNodes(Expr expr) {
        if (expr instanceof Leaf) {
            return Collections.emptyList();
        }

        if (allChildrenAreLeafNodes(expr)) {
            return Collections.singletonList(expr);
        }

        return expr.getChildren().stream()
                .map(ParserUtil::getLeafParentNodes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static List<Expr> split(Expr expr) {
        if (expr instanceof Node) {
            if (operatorIsOr((Node) expr)) {
                return Collections.singletonList(expr);
            }

            if (operatorIsAnd((Node) expr)) {
                return expr.getChildren().stream()
                        .map(ParserUtil::split)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }

        return Collections.singletonList(expr);
    }

    public static boolean isExpression(String script) {
        if (script == null) {
            return false;
        }

        try {
            Pratt.parse(script);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static boolean allChildrenAreLeafNodes(Expr expr) {
        for (Expr child : expr.getChildren()) {
            if (!(child instanceof Leaf)) {
                return false;
            }
        }
        return true;
    }

    private static boolean operatorIsAnd(Node expr) {
        return expr.getOperator().getType().equals(AviatorTokenType.LOGIC_AND);
    }

    private static boolean operatorIsOr(Node expr) {
        return expr.getOperator().getType().equals(AviatorTokenType.LOGIC_OR);
    }
}
