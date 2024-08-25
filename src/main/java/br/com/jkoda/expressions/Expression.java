package br.com.jkoda.expressions;

public interface Expression {
    <R> R accept(ExpressionVisitor<R> visitor);
}
