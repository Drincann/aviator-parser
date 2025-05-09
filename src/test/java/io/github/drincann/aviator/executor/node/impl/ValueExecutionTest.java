package io.github.drincann.aviator.executor.node.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.executor.SimpleAviatorRuntime;
import io.github.drincann.aviator.parser.Pratt;

class ValueExecutionTest {

    @Test
    public void scopedVarsShouldNotNeeded() {
        ValueExecution exec =
                new ValueExecution(new SimpleAviatorRuntime(), Pratt.parse("A == 1 && seq.any(seq.list(1, 2, 3), lambda (x, y) -> x == y && true == false == C end)"));
        System.out.println(exec.identifiers);
        assertArrayEquals(toArr(exec.identifiers), new String[] { "A", "C" });
    }

    private String[] toArr(Set<String> identifiers) {
        return identifiers.toArray(new String[0]);
    }
}