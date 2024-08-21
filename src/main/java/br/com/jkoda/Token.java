package br.com.jkoda;

public record Token(
        TokenType type,
        String lexeme,
        Object literal,
        int line
) {

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
