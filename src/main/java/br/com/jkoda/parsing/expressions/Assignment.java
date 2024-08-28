package br.com.jkoda.parsing.expressions;

import br.com.jkoda.scanning.Token;

public record Assignment(Token name, Expression value) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
