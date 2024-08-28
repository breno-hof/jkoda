package br.com.jkoda.parsing;

import br.com.jkoda.parsing.expressions.*;

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

    @Override
    public String visit(Variable variable) {
        var value = Objects.isNull(variable.name().literal()) ? "nil" : variable.name().literal().toString();
        return "var (" + variable.name().lexeme() + value + ")";
    }

    @Override
    public String visit(Assignment assignment) {
        return parenthesize("= " + assignment.name().lexeme(), assignment.value());
    }
}
