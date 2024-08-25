package br.com.jkoda.expressions;

import br.com.jkoda.Token;

public record Unary(
        Token operator,
        Expression right
) implements Expression {

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
