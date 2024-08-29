package br.com.jkoda.evaluating;

import br.com.jkoda.jKoda;
import br.com.jkoda.parsing.expressions.*;
import br.com.jkoda.parsing.statement.*;
import br.com.jkoda.scanning.Token;

import java.util.List;
import java.util.function.Supplier;

public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor {
    private Environment environment = new Environment();

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            jKoda.runtimeError(error);
        }
    }

    @Override
    public void visit(Print print) {
        System.out.println(stringify(evaluate(print.expression())));
    }

    @Override
    public void visit(Formula formula) {
        evaluate(formula.expression());
    }

    @Override
    public void visit(Var var) {
        Object value = null;
        if (var.initializer() != null) {
            value = evaluate(var.initializer());
        }

        environment.define(var.name().lexeme(), value);
    }

    @Override
    public void visit(Block block) {
        executeBlock(block.statements(), new Environment(environment));
    }

    @Override
    public Object visit(Binary binary) {
        Object right = evaluate(binary.right());
        Object left = evaluate(binary.left());

        return switch (binary.operator().type()) {
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            case GREATER -> doBinaryOperationWithCheck(binary, left, right, () -> (double)left > (double)right);
            case GREATER_EQUAL -> doBinaryOperationWithCheck(binary, left, right, () -> (double)left >= (double)right);
            case LESS -> doBinaryOperationWithCheck(binary, left, right, () -> (double)left < (double)right);
            case LESS_EQUAL -> doBinaryOperationWithCheck(binary, left, right, () ->(double)left <= (double)right);
            case MINUS -> doBinaryOperationWithCheck(binary, left, right, () -> (double)left - (double)right);
            case PLUS -> doPlusOperation(left, right);
            case SLASH -> doBinaryOperationWithCheck(binary, left, right, () -> (double)left / (double)right);
            case STAR -> doBinaryOperationWithCheck(binary, left, right, () -> (double)left * (double)right);
            default -> null;
        };
    }

    @Override
    public Object visit(Literal literal) {
        return literal.value();
    }

    @Override
    public Object visit(Grouping grouping) {
        return evaluate(grouping.expression());
    }

    @Override
    public Object visit(Unary unary) {
        Object right = evaluate(unary.right());

        return switch (unary.operator().type()) {
            case BANG -> !isTruthy(right);
            case MINUS -> {
                checkNumberOperand(unary.operator(), right);
                yield -(double)right;
            }
            default -> null;
        };
    }

    @Override
    public Object visit(Variable variable) {
        return environment.get(variable.name());
    }

    @Override
    public Object visit(Assignment assignment) {
        return environment.assign(assignment.name(), evaluate(assignment.value()));
    }

    private void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;

            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Object doBinaryOperationWithCheck(Binary binary, Object left, Object right, Supplier<Object> operation) {
        checkNumberOperands(binary.operator(), left, right);
        return operation.get();
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    private void execute(Statement statement) {
        statement.accept(this);
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private Object doPlusOperation(Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return (double)left + (double)right;
        }

        if (left instanceof String && right instanceof String) {
            return left + (String)right;
        }

        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }
}
