package br.com.jkoda.parsing.statement;

import br.com.jkoda.parsing.expressions.Expression;

public record If(Expression condition, Statement thenBranch, Statement elseBranch) implements Statement {
    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}
