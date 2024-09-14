package br.com.jkoda.interpreting.functions;

import br.com.jkoda.interpreting.Environment;
import br.com.jkoda.interpreting.Interpreter;
import br.com.jkoda.interpreting.RuntimeReturn;
import br.com.jkoda.parsing.statement.Function;

import java.util.List;

public class RuntimeFunction implements Callable {
    private final Function declaration;
    private final Environment closure;

    public RuntimeFunction(Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params().size(); i++) {
            environment.define(declaration.params().get(i).lexeme(), arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body(), environment);
        } catch (RuntimeReturn runtimeReturn) {
            return runtimeReturn.getValue();
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.params().size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name().lexeme() + ">";
    }
}
