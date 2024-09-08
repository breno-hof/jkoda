package br.com.jkoda.interpreting;

import br.com.jkoda.scanning.Token;

public class RuntimeError extends RuntimeException {
    private final Token operator;

    public RuntimeError(Token operator, String message) {
        super(message);
        this.operator = operator;
    }

    public Token getOperator() {
        return operator;
    }
}
