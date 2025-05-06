package io.github.drincann.aviator.executor;

import java.util.Map;

import com.googlecode.aviator.AviatorEvaluator;
import io.github.drincann.aviator.executor.runtime.ExpressionRuntime;

public class SimpleAviatorRuntime implements ExpressionRuntime {
    @Override
    public Object run(String expression, Map<String, Object> context) {
        return AviatorEvaluator.compile(expression, true).execute(context);
    }
}
