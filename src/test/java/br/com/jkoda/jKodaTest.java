package br.com.jkoda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class jKodaTest {

    @Test
    void Should_ReadFileLines_When_RunFile() {
        String[] args = {"src/test/resources/source_code.koda"};

        assertDoesNotThrow(() -> jKoda.main(args));
    }

    @Test
    void Should_PrintFibonacciSequence_When_ReadFibonacciFile() {
        String[] args = {"src/test/resources/fibonacci.koda"};

        assertDoesNotThrow(() -> jKoda.main(args));
    }
}