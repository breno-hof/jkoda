package br.com.jkoda.syntatic.expressions;

import br.com.jkoda.lexical.Token;

public record Unary(
        Token operator,
        Expression right
) implements Expression {

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
