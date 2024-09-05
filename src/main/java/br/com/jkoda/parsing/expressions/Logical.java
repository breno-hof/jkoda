package br.com.jkoda.parsing.expressions;


import br.com.jkoda.scanning.Token;

public record Logical(Expression left, Token operator, Expression right) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
