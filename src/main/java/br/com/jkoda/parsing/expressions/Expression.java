package br.com.jkoda.parsing.expressions;

public interface Expression {
    <R> R accept(ExpressionVisitor<R> visitor);
}
