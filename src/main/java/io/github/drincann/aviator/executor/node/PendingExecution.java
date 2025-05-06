package io.github.drincann.aviator.executor.node;

public interface PendingExecution {

    PendingExecution provide(String symbol, Object value);

    boolean canExecute();

    boolean execute();
}
