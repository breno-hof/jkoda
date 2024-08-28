package br.com.jkoda.evaluating;

import br.com.jkoda.scanning.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    void define(String name, Object value) {
        values.put(name, value);
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme())) {
            return values.get(name.lexeme());
        }

        throw new RuntimeError(name,"Undefined variable '" + name.lexeme() + "'.");
    }

    public Object assign(Token name, Object value) {
        if (values.containsKey(name.lexeme())) {
            values.put(name.lexeme(), value);
            return value;
        }

        throw new RuntimeError(name,"Undefined variable '" + name.lexeme() + "'.");
    }
}
