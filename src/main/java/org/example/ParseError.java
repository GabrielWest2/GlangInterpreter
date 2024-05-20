package org.example;

public class ParseError extends RuntimeException {
    public ParseError(String msg){
        super(msg);
    }
}