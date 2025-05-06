package io.github.drincann.aviator.executor;

import java.util.List;

import io.github.drincann.aviator.executor.node.PendingExecution;
import io.github.drincann.aviator.executor.node.impl.AndExecution;
import io.github.drincann.aviator.executor.node.impl.ConditionalExecution;
import io.github.drincann.aviator.executor.node.impl.NotExecution;
import io.github.drincann.aviator.executor.node.impl.OrExecution;
import io.github.drincann.aviator.executor.node.impl.ValueExecution;
import io.github.drincann.aviator.executor.runtime.ExpressionRuntime;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.parser.Pratt;
import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.Node;

public class AviatorPendingExecutionFactory {

    public static PendingExecution build(ExpressionRuntime runtime, Expr expression) {
        if (expression instanceof Node) {
            Node node = (Node) expression;
            if (node.getOperator().getType() == AviatorTokenType.LOGIC_AND) {
                List<Expr> children = node.getChildren();
                Expr left = children.get(0);
                Expr right = children.get(1);
                return new AndExecution(
                        AviatorPendingExecutionFactory.build(runtime, left),
                        AviatorPendingExecutionFactory.build(runtime, right)
                );
            }

            if (node.getOperator().getType() == AviatorTokenType.LOGIC_OR) {
                List<Expr> children = node.getChildren();
                Expr left = children.get(0);
                Expr right = children.get(1);
                return new OrExecution(
                        AviatorPendingExecutionFactory.build(runtime, left),
                        AviatorPendingExecutionFactory.build(runtime, right)
                );
            }

            if (node.getOperator().getType() == AviatorTokenType.LOGIC_NOT) {
                List<Expr> children = node.getChildren();
                Expr child = children.get(0);
                return new NotExecution(AviatorPendingExecutionFactory.build(runtime, child));
            }

            if (node.getOperator().getType() == AviatorTokenType.CONDITIONAL) {
                List<Expr> children = node.getChildren();
                Expr condition = children.get(0);
                Expr thenExpr = children.get(1);
                Expr elseExpr = children.get(2);
                return new ConditionalExecution(
                        AviatorPendingExecutionFactory.build(runtime, condition),
                        AviatorPendingExecutionFactory.build(runtime, thenExpr),
                        AviatorPendingExecutionFactory.build(runtime, elseExpr)
                );
            }
        }

        return new ValueExecution(runtime, expression);
    }

    public static PendingExecution compile(ExpressionRuntime runtime, String expression) {
        return build(runtime, Pratt.parse(expression));
    }
}
