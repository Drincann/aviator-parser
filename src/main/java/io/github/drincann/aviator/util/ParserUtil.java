package io.github.drincann.aviator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.Leaf;

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

    private static boolean allChildrenAreLeafNodes(Expr expr) {
        for (Expr child : expr.getChildren()) {
            if (!(child instanceof Leaf)) {
                return false;
            }
        }
        return true;
    }
}
