package io.github.drincann.aviator.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.drincann.aviator.executor.node.PendingExecution;

class PendingExecutionTest {

    @Test
    public void testShortCircuit1() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new SimpleAviatorRuntime(), "A || B && C");

        assertFalse(pending.canExecute());

        pending.provide("A", true);
        assertTrue(pending.canExecute());

        assertEquals(true, pending.execute());
    }

    @Test
    public void testShortCircuit2() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new SimpleAviatorRuntime(), "A || !(B && C)");

        assertFalse(pending.canExecute());

        pending.provide("B", false);
        assertTrue(pending.canExecute());

        assertEquals(true, pending.execute());
    }

    @Test
    public void testShortCircuit3() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new SimpleAviatorRuntime(), "(A || !(B && C))");

        assertFalse(pending.canExecute());

        pending.provide("B", false);
        assertTrue(pending.canExecute());

        assertEquals(true, pending.execute());
    }

    @Test
    public void testShortCircuit4() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new SimpleAviatorRuntime(), "(A == 1 || !(B == '2' && C > 3))");

        assertFalse(pending.canExecute());

        pending.provide("B", "2");
        assertFalse(pending.canExecute());

        pending.provide("C", 3);
        assertTrue(pending.canExecute());

        assertEquals(true, pending.execute());
    }

    @Test
    public void testShortCircuit5() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new SimpleAviatorRuntime(), "(A == 1 || !(B == '2' && C > 3))");

        assertFalse(pending.canExecute());

        pending.provide("B", "2");
        assertFalse(pending.canExecute());

        pending.provide("C", 4);
        assertFalse(pending.canExecute());

        pending.provide("A", 0);
        assertTrue(pending.canExecute());

        assertEquals(false, pending.execute());
    }

    @Test
    public void testConditionalShortCircuit1() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new SimpleAviatorRuntime(), "A || B ? C && D : E && F");
        assertFalse(pending.canExecute());

        pending.provide("A", true);
        assertFalse(pending.canExecute());

        pending.provide("C", false);
        assertTrue(pending.canExecute());

        assertEquals(false, pending.execute());
    }

    @Test
    public void testConditionalShortCircuit2() {
        PendingExecution pending = AviatorPendingExecutionFactory.compile(new SimpleAviatorRuntime(), "A && B ? C || D : E || F");
        assertFalse(pending.canExecute());

        pending.provide("A", false);
        assertFalse(pending.canExecute());

        pending.provide("E", true);
        assertTrue(pending.canExecute());

        assertEquals(true, pending.execute());
    }
}