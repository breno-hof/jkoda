package br.com.jkoda.interpreting;

public class RuntimeReturn extends RuntimeException {
    private final Object value;

    public RuntimeReturn(Object value) {
        super(null, null, false, false);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
