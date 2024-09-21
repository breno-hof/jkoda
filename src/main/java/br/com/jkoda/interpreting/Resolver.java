package br.com.jkoda.interpreting;

import br.com.jkoda.Koda;
import br.com.jkoda.parsing.expressions.*;
import br.com.jkoda.parsing.statement.*;
import br.com.jkoda.scanning.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements StatementVisitor, ExpressionVisitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Statement statement) {
        statement.accept(this);
    }

    private void resolve(Expression expression) {
        expression.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        scopes.peek().put(name.lexeme(), false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;

        scopes.peek().put(name.lexeme(), true);
    }

    private void resolveLocal(Expression expression, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme())) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolveFunction(Function function) {
        beginScope();
        for (Token param : function.params()) {
            declare(param);
            define(param);
        }
        resolve(function.body());
        endScope();
    }

    @Override
    public Void visit(Binary binary) {
        return null;
    }

    @Override
    public Void visit(Literal literal) {
        return null;
    }

    @Override
    public Void visit(Grouping grouping) {
        return null;
    }

    @Override
    public Void visit(Unary unary) {
        return null;
    }

    @Override
    public Void visit(Variable variable) {
        if (!scopes.isEmpty() && scopes.peek().get(variable.name().lexeme()) == Boolean.FALSE) {
            Koda.error(variable.name(),  "Can't read local variable in its own initializer.");
        }

        resolveLocal(variable, variable.name());
        return null;
    }

    @Override
    public Void visit(Assignment assignment) {
        resolve(assignment.value());
        resolveLocal(assignment, assignment.name());
        return null;
    }

    @Override
    public Void visit(Logical logical) {
        return null;
    }

    @Override
    public Void visit(Call call) {
        return null;
    }

    @Override
    public void visit(Print print) {
        resolve(print.expression());
    }

    @Override
    public void visit(Formula formula) {
        resolve(formula.expression());
    }

    @Override
    public void visit(Var var) {
        declare(var.name());
        if (var.initializer() != null) {
            resolve(var.initializer());
        }
        define(var.name());
    }

    @Override
    public void visit(Block block) {
        beginScope();
        resolve(block.statements());
        endScope();
    }

    @Override
    public void visit(If anIf) {
        resolve(anIf.condition());
        resolve(anIf.thenBranch());
        if (anIf.elseBranch() != null) resolve(anIf.elseBranch());
    }

    @Override
    public void visit(While aWhile) {
        resolve(aWhile.condition());
        resolve(aWhile.body());
    }

    @Override
    public void visit(Function function) {
        declare(function.name());
        define(function.name());

        resolveFunction(function);
    }

    @Override
    public void visit(Return aReturn) {
        if (aReturn.value() != null) resolve(aReturn.value());
    }
}
