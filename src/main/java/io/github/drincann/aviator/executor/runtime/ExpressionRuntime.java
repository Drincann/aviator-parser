package io.github.drincann.aviator.executor.runtime;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 表达式运行时，构造 ValueExecution 时需要指定一个实现。
 */
public interface ExpressionRuntime {

    default Set<String> getBuiltinIdentifiers() {
        return BuiltinFunctionsSet.SINGLETON;
    }

    Object run(String expression, Map<String, Object> context);
}
