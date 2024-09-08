package br.com.jkoda.interpreting.functions;

import br.com.jkoda.interpreting.Interpreter;

import java.util.List;

public class NativeClock implements Callable {
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000.0;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public String toString() {
        return  "<native fn>";
    }
}
