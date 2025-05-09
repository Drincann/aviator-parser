package io.github.drincann.aviator.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ScopedSet<T> implements Set<T> {
    private final ScopedSet<T> parent;
    private final Set<T> scope;

    public Set<T> getScope() {
        return scope;
    }

    public ScopedSet(ScopedSet<T> parent, Set<T> scope) {
        this.parent = parent;
        this.scope = scope;
    }

    public static <T> ScopedSet<T> create() {
        return new ScopedSet<>(null, new HashSet<>());
    }

    public ScopedSet<T> enter() {
        return new ScopedSet<>(this, new HashSet<>());
    }

    public ScopedSet<T> leave() {
        if (parent == null) {
            throw new IllegalStateException("No parent scope to leave");
        }

        return parent;
    }

    public ScopedSet<T> addTopScope(T o) {
        if (parent != null) {
            parent.addTopScope(o);
        } else {
            scope.add(o);
        }
        return this;
    }

    @Override
    public int size() {
        if (parent != null) {
            return merge().size();
        }

        return scope.size();
    }

    @Override
    public boolean isEmpty() {
        if (parent != null && !parent.isEmpty()) {
            return false;
        }

        return scope.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (parent != null && parent.contains(o)) {
            return true;
        }

        return scope.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        if (parent != null) {
            return merge().iterator();
        }

        return scope.iterator();
    }

    @Override
    public Object[] toArray() {
        if (parent != null) {
            return merge().toArray();
        }

        return scope.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (parent != null) {
            return merge().toArray(a);
        }

        return scope.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return scope.add(t);
    }

    @Override
    public boolean remove(Object o) {
        if (parent != null) {
            parent.remove(o);
        }

        return scope.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (parent != null) {
            return parent.containsAll(c);
        }

        return scope.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return scope.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return scope.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (parent != null) {
            parent.removeAll(c);
        }

        return scope.removeAll(c);
    }

    @Override
    public void clear() {
        if (parent != null) {
            parent.clear();
        }

        scope.clear();
    }

    private Set<T> merge() {
        Set<T> merged = new HashSet<>();
        merged.addAll(parent);
        merged.addAll(scope);
        return merged;
    }
}
