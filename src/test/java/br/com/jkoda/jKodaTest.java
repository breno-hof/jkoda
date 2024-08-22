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
    void Should_LogErrorLineAndMessage_When_ReportError() {
        String[] args = {"src/test/resources/fail_source_code.koda"};

        assertThrows(RuntimeException.class, () -> jKoda.main(args));
    }
}