package br.com.jkoda.parsing.statement;

public interface Statement {
    void accept(StatementVisitor visitor);
}
