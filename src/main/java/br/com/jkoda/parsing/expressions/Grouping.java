package br.com.jkoda.parsing.expressions;

public record Grouping(Expression expression) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
