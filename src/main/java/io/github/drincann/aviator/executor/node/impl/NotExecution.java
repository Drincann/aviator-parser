package io.github.drincann.aviator.executor.node.impl;

import io.github.drincann.aviator.executor.node.PendingExecution;

/*
   Not 操作符的执行结点，不会短路，仅用于支持 Not ast node 下的 Execution
   例如对于表达式 A && !(B || C):
      AND
     /   \
    A     NOT
          |
          OR
         /  \
        B    C
   为了支持在 B/C == true 时将整个表达式立即短路为 false，需要实现 Not operator，
   否则默认行为是将 !(B || C) 反序列化为一个 ValueExecution.
 */
public class NotExecution implements PendingExecution {

    private final PendingExecution child;

    private Boolean resultCache;

    public NotExecution(PendingExecution child) {
        this.child = child;
    }

    @Override
    public PendingExecution provide(String symbol, Object value) {
        child.provide(symbol, value);
        return this;
    }

    @Override
    public boolean canExecute() {
        return child.canExecute();
    }

    @Override
    public synchronized boolean execute() {
        if (resultCache == null) {
            if (!child.canExecute()) {
                throw new RuntimeException("cannot execute");
            }
            resultCache = child.execute();
        }

        return !resultCache;
    }
}
