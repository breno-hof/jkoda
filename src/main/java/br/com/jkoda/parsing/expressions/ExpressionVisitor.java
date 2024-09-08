package br.com.jkoda.parsing.expressions;

public interface ExpressionVisitor<R> {
    R visit(Binary binary);
    R visit(Literal literal);
    R visit(Grouping grouping);
    R visit(Unary unary);
    R visit(Variable variable);
    R visit(Assignment assignment);
    R visit(Logical logical);
    R visit(Call call);
}
