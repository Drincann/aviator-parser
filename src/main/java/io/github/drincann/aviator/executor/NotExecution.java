package io.github.drincann.aviator.executor;

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
    public synchronized Object execute() {
        if (resultCache == null) {
            Object returnValue = child.execute();
            if (!(returnValue instanceof Boolean)) {
                throw new RuntimeException("type error");
            }

            resultCache = (boolean) returnValue;
        }

        return !resultCache;
    }
}
