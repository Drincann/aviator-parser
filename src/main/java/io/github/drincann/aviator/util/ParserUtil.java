package io.github.drincann.aviator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.Leaf;
import io.github.drincann.aviator.parser.ast.Node;

public class ParserUtil {
    public static List<Expr> getLeafParentNodes(Expr expr) {
        if (expr instanceof Leaf) {
            return new ArrayList<>();
        }

        if (allChildrenAreLeafNodes(expr)) {
            return new ArrayList<Expr>() {{ add(expr); }};
        }

        return expr.getChildren().stream()
                .map(ParserUtil::getLeafParentNodes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static List<Expr> split(Expr expr) {
        if (expr instanceof Node) {
            if (operatorIsOr((Node) expr)) {
                return new ArrayList<Expr>() {{ add(expr); }};
            }

            if (operatorIsAnd((Node) expr)) {
                return new ArrayList<Expr>() {{
                    addAll(expr.getChildren().stream()
                            .map(ParserUtil::split)
                            .flatMap(List::stream)
                            .collect(Collectors.toList()));
                }};
            }
        }

        return new ArrayList<Expr>() {{ add(expr); }};
    }

    public static boolean isExpression(String script) {
        if (script == null) {
            return false;
        }

        return script.contains(";");
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
