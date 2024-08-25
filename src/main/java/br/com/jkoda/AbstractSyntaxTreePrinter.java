package br.com.jkoda;

import br.com.jkoda.expressions.*;

import java.util.Objects;

public class AbstractSyntaxTreePrinter implements ExpressionVisitor<String> {

    public String print(Expression expression) {
        return expression.accept(this);
    }

    private String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expression : expressions) {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visit(Binary binary) {
        return parenthesize(binary.operator().lexeme(), binary.left(), binary.right());
    }

    @Override
    public String visit(Literal literal) {
        return Objects.isNull(literal.value()) ? "nil" : literal.value().toString();
    }

    @Override
    public String visit(Grouping grouping) {
        return parenthesize("group", grouping.expression());
    }

    @Override
    public String visit(Unary unary) {
        return parenthesize(unary.operator().lexeme(), unary.right());
    }
}
