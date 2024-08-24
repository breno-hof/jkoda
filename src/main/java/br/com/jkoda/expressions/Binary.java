package br.com.jkoda.expressions;

import br.com.jkoda.Token;

public record Binary(
        Expression left,
        Token operator,
        Expression right
) implements Expression {
}
