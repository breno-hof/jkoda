package br.com.jkoda.parsing;

import br.com.jkoda.scanning.Token;
import br.com.jkoda.scanning.TokenType;
import br.com.jkoda.parsing.expressions.Binary;
import br.com.jkoda.parsing.expressions.Grouping;
import br.com.jkoda.parsing.expressions.Literal;
import br.com.jkoda.parsing.expressions.Unary;
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