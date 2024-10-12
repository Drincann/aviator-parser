package io.github.drincann.aviator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.Test;

class CollectionUtilTest {
    @Test
    public void testBuildMap() {
        Map<String, Integer> m = CollectionUtil.map(String.class, Integer.class, "a", 1, "b", 2, "c", 3);

        assertEquals(3, m.size());
        assertEquals(1, m.get("a"));
        assertEquals(2, m.get("b"));
        assertEquals(3, m.get("c"));
    }

    @Test
    public void testBuildMapWithEmptyEntries1() {
        Map<String, Integer> m = CollectionUtil.map(String.class, Integer.class);

        assertEquals(0, m.size());
    }

    @Test
    public void testBuildMapWithOddEntries() {
        try {
            CollectionUtil.map(String.class, Integer.class, "a", 1, "b");
        } catch (IllegalArgumentException e) {
            assertEquals("entries must be even number.", e.getMessage());
            // pass
            return;
        }
        fail("Should throw IllegalArgumentException");
    }

    @Test
    public void testBuildMapWithEmptyEntries2() {
        Map<String, Integer> m = CollectionUtil.map(String.class, Integer.class, new Object[0]);

        assertEquals(0, m.size());
    }

    @Test
    public void testBuildMapWithDifferentTypes() {
        try {
            CollectionUtil.map(String.class, Integer.class, "a", 1, "b", "2", "c", "not a number");
        } catch (ClassCastException e) {
            // pass
            return;
        }

        fail("Should throw ClassCastException");
    }

    @Test
    public void testBuildMap1() {
        Map<String, Integer> m = CollectionUtil.map("a", 1, "b", 2, "c", 3);

        assertEquals(3, m.size());
        assertEquals(1, m.get("a"));
        assertEquals(2, m.get("b"));
        assertEquals(3, m.get("c"));
    }

}