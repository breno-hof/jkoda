package br.com.jkoda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

    @Test
    void Should_PrintRecursiveCountFrom3To1_When_ReadRecursiveCountFile() {
        String[] args = {"src/test/resources/recursive_count.koda"};

        assertDoesNotThrow(() -> jKoda.main(args));
    }

    @Test
    void Should_PrintRecursiveFibonacciSequence_When_ReadRecursiveFibonacciFile() {
        String[] args = {"src/test/resources/recursive_fibonacci.koda"};

        assertDoesNotThrow(() -> jKoda.main(args));
    }
}