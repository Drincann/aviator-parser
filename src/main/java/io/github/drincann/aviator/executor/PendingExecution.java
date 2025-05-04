package io.github.drincann.aviator.executor;

public interface PendingExecution {

    PendingExecution provide(String symbol, Object value);

    boolean canExecute();

    Object execute();
}
