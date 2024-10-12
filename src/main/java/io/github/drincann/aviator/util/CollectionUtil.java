package io.github.drincann.aviator.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CollectionUtil {
    public static <K, V> Map<K, V> map(Class<K> kClass, Class<V> vClass, Object... entries) {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("entries must be even number.");
        }

        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put(kClass.cast(entries[i]), vClass.cast(entries[i + 1]));
        }

        return map;
    }

    public static <K, V> Map<K, V> map(Object... entries) {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("entries must be even number.");
        }

        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put((K) entries[i], (V) entries[i + 1]);
        }

        return map;
    }

    public static <T> Set<T> set(T... elements) {
        Set<T> set = new java.util.HashSet<>();
        Collections.addAll(set, elements);
        return set;
    }
}
