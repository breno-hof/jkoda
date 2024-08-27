package br.com.jkoda.evaluating;

import br.com.jkoda.jKoda;
import br.com.jkoda.scanning.Token;
import br.com.jkoda.parsing.expressions.*;

import java.util.function.Supplier;

public class Interpreter implements ExpressionVisitor<Object> {

    public void interpret(Expression expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            jKoda.runtimeError(error);
        }
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
