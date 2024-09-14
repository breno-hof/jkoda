package br.com.jkoda.interpreting;

import br.com.jkoda.interpreting.functions.Callable;
import br.com.jkoda.interpreting.functions.NativeClock;
import br.com.jkoda.interpreting.functions.RuntimeFunction;
import br.com.jkoda.jKoda;
import br.com.jkoda.parsing.expressions.*;
import br.com.jkoda.parsing.statement.*;
import br.com.jkoda.scanning.Token;
import br.com.jkoda.scanning.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor {
    private final Environment globals = new Environment();
    private Environment environment = globals;

    public Interpreter() {
        globals.define("clock", new NativeClock());
    }

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            jKoda.runtimeError(error);
        }
    }

    public Environment getGlobals() {
        return globals;
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
    public void visit(If anIf) {
        if (isTruthy(evaluate(anIf.condition()))) {
            execute(anIf.thenBranch());
        } else if (anIf.elseBranch() != null) {
            execute(anIf.elseBranch());
        }
    }

    @Override
    public void visit(While aWhile) {
        while (isTruthy(evaluate(aWhile.condition()))) {
            execute(aWhile.body());
        }
    }

    @Override
    public void visit(Function function) {
        RuntimeFunction runtimeFunction = new RuntimeFunction(function);
        environment.define(function.name().lexeme(), runtimeFunction);
    }

    @Override
    public void visit(Return aReturn) {
        Object value = null;
        if (aReturn.value() != null) value = evaluate(aReturn.value());

        throw new RuntimeReturn(value);
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

    @Override
    public Object visit(Logical logical) {
        Object left = evaluate(logical.left());

        if (logical.operator().type() == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(logical.right());
    }

    @Override
    public Object visit(Call call) {
        Object callee = evaluate(call.callee());

        List<Object> arguments = new ArrayList<>();
        for (Expression argument : call.arguments()) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof Callable function)) {
            throw new RuntimeError(call.token(), "Can only call functions and classes.");
        }

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(call.token(), "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    public void executeBlock(List<Statement> statements, Environment environment) {
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
