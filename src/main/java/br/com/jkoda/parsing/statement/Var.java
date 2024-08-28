package br.com.jkoda.parsing.statement;

import br.com.jkoda.parsing.expressions.Expression;
import br.com.jkoda.scanning.Token;

public record Var(Token name, Expression initializer) implements Statement {
    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}
