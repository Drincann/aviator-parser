package io.github.drincann.aviator.executor.node.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.github.drincann.aviator.executor.node.PendingExecution;
import io.github.drincann.aviator.executor.runtime.ExpressionRuntime;
import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.FunctionCall;
import io.github.drincann.aviator.parser.ast.LambdaFunction;
import io.github.drincann.aviator.parser.ast.Leaf;
import io.github.drincann.aviator.parser.ast.Node;
import io.github.drincann.aviator.util.ScopedSet;

/**
 * 对于 ast 中未实现对应 execution 的 node，使用该实现.
 * 当所有依赖的标识均被 provide 方法传入时，允许执行.
 * 执行时并使用构造时指定的 runtime.
 */
public class ValueExecution implements PendingExecution {

    private final Expr node;

    private final ExpressionRuntime runtime;

    final Set<String> identifiers;

    private final Map<String, Object> context;

    private Boolean resultCache;

    public ValueExecution(ExpressionRuntime runtime, Expr node) {
        this.node = node;
        this.runtime = runtime;
        this.context = new ConcurrentHashMap<>();
        this.identifiers = extractGlobalVars(node);
    }

    private Set<String> extractGlobalVars(Expr expr) {
        ScopedSet<String> global = ScopedSet.create();
        dfs(expr, global);

        return global.getScope();
    }

    private void dfs(Expr expr, ScopedSet<String> vars) {
        if (expr instanceof Leaf) {
            AviatorToken token = ((Leaf) expr).getToken();
            if (token.getType() == AviatorTokenType.IDENTIFIER) {
                if (!vars.contains(token.getLexeme())) {
                    addIfNotBuiltin(vars, token.getLexeme());
                }
            }
        }

        if (expr instanceof Node) {
            Node node = (Node) expr;
            if (node.getOperator().getType() == AviatorTokenType.DOT) {
                Expr left = node.getChildren().get(0);
                if (isIdentifier(left)) {
                    addIfNotBuiltin(vars, left.toString());
                    return;
                }
            }
            for (Expr child : expr.getChildren()) {
                dfs(child, vars);
            }
        }

        if (expr instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) expr;
            dfs(functionCall.getFunction(), vars);
            for (Expr arg : functionCall.getArguments()) {
                dfs(arg, vars);
            }
        }

        if (expr instanceof LambdaFunction) {
            LambdaFunction lambda = (LambdaFunction) expr;
            ScopedSet<String> lambdaVars = vars.enter();
            lambdaVars.addAll(toNames(lambda.getParameters()));
            dfs(((LambdaFunction) expr).getBody(), lambdaVars);
        }
    }

    private void addIfNotBuiltin(ScopedSet<String> vars, String identifier) {
        if (runtime.getBuiltinIdentifiers().contains(identifier)) {
            return;
        }

        vars.addTopScope(identifier);
    }

    private static boolean isIdentifier(Expr left) {
        return left instanceof Leaf && ((Leaf) left).getToken().getType() == AviatorTokenType.IDENTIFIER;
    }

    private static List<String> toNames(List<Leaf> parameters) {
        return parameters.stream().map(Leaf::toString).collect(Collectors.toList());
    }

    @Override
    public PendingExecution provide(String symbol, Object value) {
        if (this.identifiers.contains(symbol)) {
            context.put(symbol, value);
        }
        return this;
    }

    @Override
    public boolean canExecute() {
        return identifiers.stream().allMatch(context::containsKey);
    }

    @Override
    public synchronized boolean execute() {
        if (resultCache == null) {
            Object result = runtime.run(node.serialize(), context);
            if (!(result instanceof Boolean)) {
                throw new RuntimeException("type error");
            }
            resultCache = (boolean) result;
        }

        return resultCache;
    }
}
