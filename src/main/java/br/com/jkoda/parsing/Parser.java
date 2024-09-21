package br.com.jkoda.parsing;

import br.com.jkoda.Koda;
import br.com.jkoda.parsing.expressions.*;
import br.com.jkoda.parsing.statement.*;
import br.com.jkoda.scanning.Token;
import br.com.jkoda.scanning.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static br.com.jkoda.scanning.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

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
            if (match(FUN)) return function("function");
            if (match(VAR)) return variable();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }

        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Statement> body = block();
        return new Function(name, parameters, body);
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
        if (match(FOR)) return aFor();
        if (match(IF)) return anIf();
        if (match(PRINT)) return print();
        if (match(RETURN)) return aReturn();
        if (match(WHILE)) return aWhile();
        if (match(LEFT_BRACE)) return new Block(block());

        return formula();
    }

    private Statement aReturn() {
        Token keyword = previous();
        Expression value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }

        consume(SEMICOLON, "Expect ';' after return value.");
        return new Return(keyword, value);
    }

    private Statement aFor() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Statement initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = variable();
        } else {
            initializer = formula();
        }

        Expression condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expression increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Statement body = statement();

        if (increment != null) body = new Block(Arrays.asList(body, new Formula(increment)));

        if (condition == null) condition = new Literal(true);

        body = new While(condition, body);

        if (initializer != null) body = new Block(Arrays.asList(initializer, body));

        return body;
    }

    private Statement aWhile() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Statement body = statement();

        return new While(condition, body);
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

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        Expression expression = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

            if (expression instanceof Variable) {
                Token name = ((Variable)expression).name();
                return new Assignment(name, value);
            }

            Koda.error(equals, "Invalid assignment target.");
        }

        return expression;
    }

    private Expression or() {
        return handleLogicalOperatorRule(this::and, OR);
    }

    private Expression and() {
        return handleLogicalOperatorRule(this::equality, AND);
    }

    private Expression equality() {
        return handleBinaryOperatorRule(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }

    private Expression comparison() {
        return handleBinaryOperatorRule(this::term, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    private Statement anIf() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Statement thenBranch = statement();
        Statement elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new If(condition, thenBranch, elseBranch);
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
        
        return call();
    }

    private Expression call() {
        Expression expression = primary();

        while (true) {
            if (match(LEFT_PAREN)) {
                expression = finishCall(expression);
            } else {
                break;
            }
        }

        return expression;
    }

    private Expression finishCall(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }

        Token rightParen = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Call(callee, rightParen, arguments);
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

    private Expression handleLogicalOperatorRule(Supplier<Expression> rule, TokenType... types) {
        var expression = rule.get();

        while (match(types)) {
            Token operator = previous();
            Expression right = rule.get();
            expression = new Logical(expression, operator, right);
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
        Koda.error(token, message);
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
