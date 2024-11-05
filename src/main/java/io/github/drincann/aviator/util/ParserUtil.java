package io.github.drincann.aviator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.drincann.aviator.parser.Pratt;

public class ParserUtil {
    public static List<Pratt.Expr> getLeafParentNodes(Pratt.Expr expr) {
        if (expr instanceof Pratt.Leaf) {
            return new ArrayList<>();
        }

        if (allChildrenAreLeafNodes(expr)) {
            return new ArrayList<Pratt.Expr>() {{ add(expr); }};
        }

        return expr.getChildren().stream()
                .map(ParserUtil::getLeafParentNodes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static boolean allChildrenAreLeafNodes(Pratt.Expr expr) {
        for (Pratt.Expr child : expr.getChildren()) {
            if (!(child instanceof Pratt.Leaf)) {
                return false;
            }
        }
        return true;
    }
}
