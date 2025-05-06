package io.github.drincann.aviator.executor.node.impl;

import io.github.drincann.aviator.executor.node.PendingExecution;

/**
 * And 操作符的执行结点，允许在一侧值为 false 时短路.
 * 例如对 A && B，当 A 为 false 时，execute 方法将返回 false.
 */
public class AndExecution implements PendingExecution {

    private final PendingExecution left;
    private final PendingExecution right;

    private Boolean resultCache;

    public AndExecution(PendingExecution left, PendingExecution right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public PendingExecution provide(String symbol, Object value) {
        left.provide(symbol, value);
        right.provide(symbol, value);
        return this;
    }

    @Override
    public boolean canExecute() {
        if (left.canExecute() && right.canExecute()) {
            return true;
        }
        if (left.canExecute() && !left.execute()) {
            return true;
        }
        if (right.canExecute() && !right.execute()) {
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean execute() {
        if (resultCache == null) {
            if (left.canExecute() && right.canExecute()) {

                resultCache = left.execute() && right.execute();
            }
            if (left.canExecute() && !left.execute()) {
                resultCache = false;
            }
            if (right.canExecute() && !right.execute()) {
                resultCache = false;
            }
        }

        if (resultCache == null) {
            throw new RuntimeException("cannot execute");
        }

        return resultCache;
    }
}
