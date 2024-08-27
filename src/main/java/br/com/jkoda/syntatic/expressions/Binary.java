package br.com.jkoda.syntatic.expressions;

import br.com.jkoda.lexical.Token;

public record Binary(
        Expression left,
        Token operator,
        Expression right
) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
