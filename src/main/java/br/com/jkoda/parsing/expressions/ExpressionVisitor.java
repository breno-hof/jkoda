package br.com.jkoda.parsing.expressions;

public interface ExpressionVisitor<R> {
    R visit(Binary binary);
    R visit(Literal literal);
    R visit(Grouping grouping);
    R visit(Unary unary);
}
