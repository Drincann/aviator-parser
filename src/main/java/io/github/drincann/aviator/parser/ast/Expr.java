package io.github.drincann.aviator.parser.ast;

import java.util.List;

public interface Expr {

    List<Expr> getChildren();

    /**
     * 序列化到逆波兰形式文本
     *
     * @return reverse Polish representation of this expression
     */
    String rp();

    /**
     * 序列化到文本
     *
     * @return Aviator expression preserving this AST's semantics
     */
    String serialize();
}
