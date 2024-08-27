package br.com.jkoda.syntatic.expressions;

public interface Expression {
    <R> R accept(ExpressionVisitor<R> visitor);
}
