package br.com.jkoda.interpreting.functions;

import br.com.jkoda.interpreting.Interpreter;

import java.util.List;

public interface Callable {
    Object call(Interpreter interpreter, List<Object> arguments);
    int arity();
}
