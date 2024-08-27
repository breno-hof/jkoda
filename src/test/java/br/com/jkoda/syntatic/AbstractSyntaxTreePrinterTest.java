package br.com.jkoda.syntatic;

import br.com.jkoda.lexical.Token;
import br.com.jkoda.lexical.TokenType;
import br.com.jkoda.syntatic.expressions.Binary;
import br.com.jkoda.syntatic.expressions.Grouping;
import br.com.jkoda.syntatic.expressions.Literal;
import br.com.jkoda.syntatic.expressions.Unary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractSyntaxTreePrinterTest {

    @Test
    void Should_ReturnSyntaxTree_When_Print() {
        var expected = "(* (- 123) (group 45.67))";
        var actor = new Binary(
                new Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Literal(123)
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Grouping(
                        new Literal(45.67)
                )
        );

        var scene = new AbstractSyntaxTreePrinter();

        var actual = scene.print(actor);

        assertEquals(expected, actual);
        System.out.printf("Should_ReturnSyntaxTree_When_Print\nExpected: %s\nActual: %s\n", expected, actual);
    }

}