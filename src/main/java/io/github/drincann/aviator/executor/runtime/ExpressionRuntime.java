package io.github.drincann.aviator.executor.runtime;

import java.util.Map;

/**
 * 表达式运行时，构造 ValueExecution 时需要指定一个实现。
 */
public interface ExpressionRuntime {
    Object run(String expression, Map<String, Object> context);
}
