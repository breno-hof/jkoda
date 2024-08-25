package br.com.jkoda.expressions;

public record Grouping(Expression expression) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
