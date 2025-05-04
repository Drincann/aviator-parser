package io.github.drincann.aviator.executor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.executor.runtime.AviatorRuntime;

class PendingExecutionTest {
    @Test
    public void testShortCircuit1() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new AviatorRuntime(), "A || B && C");

        assertFalse(pending.canExecute());

        pending.provide("A", true);
        assertTrue(pending.canExecute());

        assertEquals(true, pending.execute());
    }

    @Test
    public void testShortCircuit2() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new AviatorRuntime(), "A || !(B && C)");

        assertFalse(pending.canExecute());

        pending.provide("B", false);
        assertTrue(pending.canExecute());

        assertEquals(true, pending.execute());
    }
}