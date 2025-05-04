package io.github.drincann.aviator.executor.runtime;

import java.util.Map;

import com.googlecode.aviator.AviatorEvaluator;

public class AviatorRuntime implements ExpressionRuntime {
    @Override
    public Object run(String expression, Map<String, Object> context) {
        return AviatorEvaluator.compile(expression, true).execute(context);
    }
}
