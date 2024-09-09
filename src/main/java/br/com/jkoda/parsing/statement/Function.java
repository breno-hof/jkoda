package br.com.jkoda.parsing.statement;

import br.com.jkoda.scanning.Token;

import java.util.List;

public record Function(Token name, List<Token> params, List<Statement> body) implements Statement {
    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}
