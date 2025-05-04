package io.github.drincann.aviator.executor;

public class AndExecution implements PendingExecution {

    private final PendingExecution left;
    private final PendingExecution right;

    private Object resultCache;

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
        if (left.canExecute() && left.execute() instanceof Boolean && (boolean) left.execute() == false) {
            return true;
        }
        if (right.canExecute() && right.execute() instanceof Boolean && (boolean) right.execute() == false) {
            return true;
        }
        return false;
    }

    @Override
    public synchronized Object execute() {
        if (resultCache == null) {
            resultCache = executeWithoutCache();
        }

        return resultCache;
    }

    private Object executeWithoutCache() {
        if (resultCache != null) {
            return resultCache;
        }

        if (left.canExecute() && right.canExecute()) {
            if (!(left.execute() instanceof Boolean) || !(right.execute() instanceof Boolean)) {
                throw new RuntimeException("type error");
            }

            return (boolean) left.execute() && (boolean) right.execute();
        }
        if (left.canExecute() && left.execute() instanceof Boolean && (boolean) left.execute() == false) {
            return false;
        }
        if (right.canExecute() && right.execute() instanceof Boolean && (boolean) right.execute() == false) {
            return false;
        }

        throw new RuntimeException("cannot execute");
    }
}
