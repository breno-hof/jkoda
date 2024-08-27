package br.com.jkoda;

import br.com.jkoda.syntatic.expressions.*;

public class Interpreter implements ExpressionVisitor<Object> {
    @Override
    public Object visit(Binary binary) {
        Object right = evaluate(binary.right());
        Object left = evaluate(binary.left());

        return switch (binary.operator().type()) {
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            case GREATER -> (double)left > (double)right;
            case GREATER_EQUAL -> (double)left >= (double)right;
            case LESS -> (double)left < (double)right;
            case LESS_EQUAL -> (double)left <= (double)right;
            case MINUS -> (double)left - (double)right;
            case PLUS -> doPlusOperation(left, right);
            case SLASH -> (double)left / (double)right;
            case STAR -> (double)left * (double)right;
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
            case MINUS -> -(double)right;
            default -> null;
        };
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
