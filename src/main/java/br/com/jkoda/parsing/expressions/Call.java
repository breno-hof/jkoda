package br.com.jkoda.parsing.expressions;

import br.com.jkoda.scanning.Token;

import java.util.List;

public record Call(Expression callee, Token token, List<Expression> arguments) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
