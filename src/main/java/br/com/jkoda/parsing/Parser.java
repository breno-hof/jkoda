package br.com.jkoda.parsing;

import br.com.jkoda.jKoda;
import br.com.jkoda.scanning.Token;
import br.com.jkoda.parsing.expressions.*;

import java.util.List;
import java.util.function.Supplier;

import static br.com.jkoda.scanning.TokenType.*;
import br.com.jkoda.scanning.TokenType;


public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expression parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expression expression() {
        return equality();
    }

    private Expression equality() {
        return handleBinaryOperatorRule(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }

    private Expression comparison() {
        return handleBinaryOperatorRule(this::term, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    private Expression term() {
        return handleBinaryOperatorRule(this::factor, MINUS, PLUS);
    }

    private Expression factor() {
        return handleBinaryOperatorRule(this::unary, SLASH, STAR);
    }

    private Expression unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Unary(operator, right);
        }
        
        return primary();
    }

    private Expression primary() {
        if (match(FALSE)) return new Literal(false);
        if (match(TRUE)) return new Literal(true);
        if (match(NIL)) return new Literal(null);

        if (match(NUMBER, STRING)) {
            return new Literal(previous().literal());
        }

        if (match(LEFT_PAREN)) {
            Expression expression = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Grouping(expression);
        }

        throw error(peek(), "Expect expression.");
    }

    private Expression handleBinaryOperatorRule(Supplier<Expression> rule, TokenType... types) {
        var expression = rule.get();

        while (match(types)) {
            Token operator = previous();
            Expression right = rule.get();
            expression = new Binary(expression, operator, right);
        }

        return expression;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        jKoda.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type() == SEMICOLON) return;

            switch (peek().type()) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {}
            }

            advance();
        }
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
