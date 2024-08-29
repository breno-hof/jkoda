package br.com.jkoda.parsing;

import br.com.jkoda.jKoda;
import br.com.jkoda.parsing.expressions.*;
import br.com.jkoda.parsing.statement.*;
import br.com.jkoda.scanning.Token;
import br.com.jkoda.scanning.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static br.com.jkoda.scanning.TokenType.*;


public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        Expression expression = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

            if (expression instanceof Variable) {
                Token name = ((Variable)expression).name();
                return new Assignment(name, value);
            }

            jKoda.error(equals, "Invalid assignment target.");
        }

        return expression;
    }

    private Expression equality() {
        return handleBinaryOperatorRule(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }

    private Expression comparison() {
        return handleBinaryOperatorRule(this::term, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse() {
        var statements = new ArrayList<Statement>();
        while(!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Statement declaration() {
        try {
            if (match(VAR)) return variable();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement variable() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expression initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Var(name, initializer);
    }

    private Statement statement() {
        if (match(PRINT)) return print();
        if (match(LEFT_BRACE)) return new Block(block());

        return formula();
    }

    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Statement print() {
        Expression expression = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Print(expression);
    }

    private Statement formula() {
        Expression expression = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Formula(expression);
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

        if (match(IDENTIFIER)) {
            return new Variable(previous());
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
