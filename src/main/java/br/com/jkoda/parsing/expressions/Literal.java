package br.com.jkoda.parsing.expressions;

public record Literal(Object value) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
