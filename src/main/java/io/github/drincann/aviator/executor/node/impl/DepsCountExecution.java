package io.github.drincann.aviator.executor.node.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.drincann.aviator.executor.node.PendingExecution;
import io.github.drincann.aviator.executor.runtime.ExpressionRuntime;

public class DepsCountExecution implements PendingExecution {

    final private Map<String, Object> context = new ConcurrentHashMap<>();

    final private int deps;

    final private ExpressionRuntime runtime;

    private Boolean resultCache;

    private String expression;

    public DepsCountExecution(ExpressionRuntime runtime, String expression, int deps) {
        this.runtime = runtime;
        this.expression = expression;
        this.deps = deps;
    }

    @Override
    public PendingExecution provide(String symbol, Object value) {
        context.put(symbol, value);
        return this;
    }

    @Override
    public boolean canExecute() {
        return context.size() >= deps;
    }

    @Override
    public synchronized boolean execute() {
        if (resultCache == null) {
            Object result = runtime.run(expression, context);
            if (!(result instanceof Boolean)) {
                throw new RuntimeException("type error");
            }
            resultCache = (boolean) result;
        }

        return resultCache;
    }
}
