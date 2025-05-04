package io.github.drincann.aviator.executor.runtime;

import java.util.Map;

public interface ExpressionRuntime {
    Object run(String expression, Map<String, Object> context);
}
