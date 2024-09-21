package br.com.jkoda.parsing.statement;

public interface StatementVisitor {
    void visit(Print print);
    void visit(Formula formula);
    void visit(Var var);
    void visit(Block block);
    void visit(If anIf);
    void visit(While aWhile);
    void visit(Function function);
    void visit(Return aReturn);
}
