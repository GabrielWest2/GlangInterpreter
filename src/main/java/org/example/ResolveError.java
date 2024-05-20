package org.example;

public class ResolveError extends RuntimeException {
    public ResolveError(String msg){
        super(msg);
    }
}