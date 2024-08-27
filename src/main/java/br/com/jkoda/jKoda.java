package br.com.jkoda;

import br.com.jkoda.evaluating.Interpreter;
import br.com.jkoda.evaluating.RuntimeError;
import br.com.jkoda.scanning.Scanner;
import br.com.jkoda.scanning.Token;
import br.com.jkoda.scanning.TokenType;
import br.com.jkoda.parsing.AbstractSyntaxTreePrinter;
import br.com.jkoda.parsing.Parser;
import br.com.jkoda.parsing.expressions.Expression;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class jKoda {
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jkoda [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expression expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        System.out.println(new AbstractSyntaxTreePrinter().print(expression));

        interpreter.interpret(expression);
    }

    public static void error(Token token, String message) {
        if (token.type() == TokenType.EOF) {
            report(token.line(), " at end", message);
        }

        report(token.line(), " at '" + token.lexeme() + "'", message);
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println("[line " + error.getOperator().line() + "]" + error.getMessage());
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}