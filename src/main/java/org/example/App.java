package org.example;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static List<Token> tokens;
    public static String program;
    public static Interpreter i;
    public static void main( String[] args ) throws FileNotFoundException {
        program = new Scanner(new File("program.gl")).useDelimiter("\\Z").next();

        Tokenizer tokenizer = new Tokenizer(program);
        i = new Interpreter();
        Parser p = new Parser(tokenizer.tokenize());
        List<Stmt> statements = p.parse();
        Resolver r = new Resolver(i);
        r.resolve(statements);
        try {
            i.interpret(statements);
        }catch (StackOverflowError e){
            System.err.println("Runtime Error: Stack Overflow");
        }
    }

    private static String getProgramLine(int linenum){
        return StringUtils.split(program, "\n")[linenum - 1];
    }

    public static void runtimeError(Token t, String message){
        System.err.println("Runtime Error: " + message);
        printErrorLine(t);
    }

    public static void parseError(Token t, String message){
        System.err.println("Parse Error: " + message);
        printErrorLine(t);
    }

    public static void resolveError(Token t, String message){
        System.err.println("Resolution Error: " + message);
        printErrorLine(t);
    }

    private static void printErrorLine(Token t) {
        String lineNum = String.valueOf(t.getLine());
        System.err.println(lineNum + " | " + getProgramLine(t.getLine()));
        // Print out arrows pointing at the offending token
        int numSpaces = 3 + lineNum.length() + t.getHorizontal()-t.getLexeme().length();
        System.err.println(" ".repeat(Math.max(0, numSpaces)) + "^^^");
        System.exit(-1);
    }
}
