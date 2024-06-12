package org.example;

/**
 * Allows a return value to be sent down the stack,
 * as there is otherwise no mechanism to keep track
 * of values produced by the execution of arbitrary
 * statements
 * @see Stmt
 */
public class BreakFrom extends RuntimeException{
    public BreakFrom(){
        super(null, null, false, false);
    }
}
