package br.com.jkoda.parsing.statement;

import java.util.List;

public record Block(List<Statement> statements) implements Statement {
    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}
