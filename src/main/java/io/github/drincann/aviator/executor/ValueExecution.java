package io.github.drincann.aviator.executor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.drincann.aviator.executor.runtime.ExpressionRuntime;
import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.Leaf;

public class ValueExecution implements PendingExecution {

    private final Expr node;

    private final ExpressionRuntime runtime;

    private final Set<String> identifiers;

    private final Map<String, Object> context;

    private Object resultCache;

    public ValueExecution(ExpressionRuntime runtime, Expr node) {
        this.node = node;
        this.runtime = runtime;

        this.context = new ConcurrentHashMap<>();

        this.identifiers = new HashSet<>();

        node.walk(expr -> {
            if (expr instanceof Leaf) {
                AviatorToken token = ((Leaf) expr).getToken();
                if (token.getType() == AviatorTokenType.IDENTIFIER) {
                    identifiers.add(token.getLexeme());
                }
            }
        });
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
    public synchronized Object execute() {
        if (resultCache == null) {
            resultCache = runtime.run(node.serialize(), context);
        }

        return resultCache;
    }
}
