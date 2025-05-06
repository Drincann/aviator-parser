package io.github.drincann.aviator.executor.node.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.drincann.aviator.executor.node.PendingExecution;
import io.github.drincann.aviator.executor.runtime.ExpressionRuntime;
import io.github.drincann.aviator.lexer.token.AviatorToken;
import io.github.drincann.aviator.lexer.token.AviatorTokenType;
import io.github.drincann.aviator.parser.ast.Expr;
import io.github.drincann.aviator.parser.ast.Leaf;

/**
 * 对于 ast 中未实现对应 execution 的 node，使用该实现.
 * 当所有依赖的标识均被 provide 方法传入时，允许执行.
 * 执行时并使用构造时指定的 runtime.
 */
public class ValueExecution implements PendingExecution {

    private final Expr node;

    private final ExpressionRuntime runtime;

    private final Set<String> identifiers;

    private final Map<String, Object> context;

    private Boolean resultCache;

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
