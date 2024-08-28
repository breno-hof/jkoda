package br.com.jkoda.parsing.expressions;

import br.com.jkoda.scanning.Token;

public record Variable(Token name) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
