package io.github.drincann.aviator.executor.runtime;

import java.util.HashSet;

public class BuiltinFunctionsSet extends HashSet<String> {
    public static final BuiltinFunctionsSet SINGLETON = new BuiltinFunctionsSet();

    static {
        SINGLETON.add("assert");
        SINGLETON.add("sysdate");
        SINGLETON.add("rand");
        SINGLETON.add("cmp");
        SINGLETON.add("print");
        SINGLETON.add("println");
        SINGLETON.add("p");
        SINGLETON.add("pst");
        SINGLETON.add("now");
        SINGLETON.add("long");
        SINGLETON.add("double");
        SINGLETON.add("boolean");
        SINGLETON.add("str");
        SINGLETON.add("bigint");
        SINGLETON.add("decimal");
        SINGLETON.add("identity");
        SINGLETON.add("type");
        SINGLETON.add("is_a");
        SINGLETON.add("is_def");
        SINGLETON.add("undef");
        SINGLETON.add("range");
        SINGLETON.add("tuple");
        SINGLETON.add("eval");
        SINGLETON.add("comparator");
        SINGLETON.add("max");
        SINGLETON.add("min");
        SINGLETON.add("constantly");
        SINGLETON.add("repeat");
        SINGLETON.add("repeatedly");
        SINGLETON.add("count");
        SINGLETON.add("is_empty");
        SINGLETON.add("distinct");
        SINGLETON.add("is_distinct");
        SINGLETON.add("concat");
        SINGLETON.add("include");
        SINGLETON.add("sort");
        SINGLETON.add("reverse");
        SINGLETON.add("reduce");
        SINGLETON.add("take_while");
        SINGLETON.add("drop_while");
        SINGLETON.add("group_by");
        SINGLETON.add("zipmap");
        SINGLETON.add("map");
        SINGLETON.add("filter");
        SINGLETON.add("load");
        SINGLETON.add("require");

        SINGLETON.add("string");
        SINGLETON.add("math");
        SINGLETON.add("seq");
    }
}
