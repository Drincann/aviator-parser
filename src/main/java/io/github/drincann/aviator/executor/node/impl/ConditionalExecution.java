package io.github.drincann.aviator.executor.node.impl;

import io.github.drincann.aviator.executor.node.PendingExecution;

/**
 * 三元条件操作符的执行结点，允许在条件可执行且对应分支可执行的情况下短路.
 * 例如对 A || B，当 A 为 true 时，execute 方法将返回 true.
 */
public class ConditionalExecution implements PendingExecution {

    private final PendingExecution condition;
    private final PendingExecution thenExec;
    private final PendingExecution elseExec;

    private Boolean resultCache;

    public ConditionalExecution(PendingExecution condition, PendingExecution thenExec, PendingExecution elseExec) {
        this.condition = condition;
        this.thenExec = thenExec;
        this.elseExec = elseExec;
    }

    @Override
    public PendingExecution provide(String symbol, Object value) {
        this.condition.provide(symbol, value);
        this.thenExec.provide(symbol, value);
        this.elseExec.provide(symbol, value);
        return this;
    }

    @Override
    public boolean canExecute() {
        if (condition.canExecute() && condition.execute() && thenExec.canExecute()) {
            return true;
        }

        if (condition.canExecute() && !condition.execute() && elseExec.canExecute()) {
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean execute() {
        if (resultCache == null) {
            if (condition.canExecute() && condition.execute() && thenExec.canExecute()) {
                resultCache = thenExec.execute();
            }

            if (condition.canExecute() && !condition.execute() && elseExec.canExecute()) {
                resultCache = elseExec.execute();
            }
        }

        if (resultCache == null) {
            throw new RuntimeException("cannot execute");
        }

        return resultCache;
    }
}
