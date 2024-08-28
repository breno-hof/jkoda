package br.com.jkoda.parsing.statement;

import br.com.jkoda.parsing.expressions.Expression;

public record Print(Expression expression) implements Statement {
    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}
