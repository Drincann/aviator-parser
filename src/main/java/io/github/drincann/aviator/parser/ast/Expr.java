package io.github.drincann.aviator.parser.ast;

import java.util.List;
import java.util.function.Consumer;

public interface Expr {

    List<Expr> getChildren();

    /**
     * 序列化到逆波兰形式文本
     */
    String rp();

    /**
     * 序列化到文本
     */
    String serialize();

    default Expr walk(Consumer<Expr> walker) {
        for (Expr child : this.getChildren()) {
            walker.accept(child.walk(walker));
        }

        walker.accept(this);

        return this;
    }
}
