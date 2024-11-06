package io.github.drincann.aviator.parser.ast;

import java.util.List;

public interface Expr {
    String rp();
    List<Expr> getChildren();
}
