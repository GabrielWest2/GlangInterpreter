package org.example;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static List<Token> tokens = new ArrayList<>();
    public static String program;
    public static Interpreter i;
    public static void main( String[] args ) throws FileNotFoundException {
        if(args.length != 1){
            System.err.println("Usage: glang <program>");
            return;
        }

        i = new Interpreter();

        program = new Scanner(new File("wrapper.gl")).useDelimiter("\\Z").next();;
        tokens.addAll(StandardLibCreator.getWrapperCode(program));
        program = new Scanner(new File(args[0])).useDelimiter("\\Z").next();
        tokens.addAll(new Tokenizer(program).tokenize(true));

        Parser p = new Parser(tokens);
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
        try {
            return StringUtils.split(program, "\n")[linenum - 1];

        }finally {
            return "Invalid line";
        }
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
        String lineNum = String.valueOf(t.line());
        System.err.println(lineNum + " | " + getProgramLine(t.line()));
        // Print out arrows pointing at the offending token
        int numSpaces = 3 + lineNum.length() + t.horizontal()-t.lexeme().length();
        System.err.println(" ".repeat(Math.max(0, numSpaces)) + "^^^");
        System.exit(-1);
    }
}
