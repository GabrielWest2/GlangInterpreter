package org.example;

import java.util.List;

public class GFunction implements GCallable{
    private final Environment closure;
    private final Stmt.Function declaration;
    private final boolean isInitializer;
    private final boolean isWrapped;
    /**
     * Create new function
     *
     * @param declaration   the {@code Stmt.Function} statement in which this function was defined
     * @param closure       persistent local scope
     * @param isInitializer is the function a constructor for a class?
     * @see Stmt.Function
     */
    public GFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
        this.isWrapped = false;
    }

    /**
     * Create new function
     *
     * @param declaration   the {@code Stmt.Function} statement in which this function was defined
     * @param closure       persistent local scope
     * @param isInitializer is the function a constructor for a class?
     * @see Stmt.Function
     */
    public GFunction(Stmt.Function declaration, Environment closure, boolean isInitializer, boolean isWrapped) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
        this.isWrapped = isWrapped;
    }

    /**
     * Get the arity of this function
     * @return the number of parameters required
     */
    @Override
    public int arity() {
        return declaration.params.size();
    }


    /**
     * Call this function
     * @param interpreter
     * @param arguments
     * @return return value of function, or {@code null} if none
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for(int i =0; i < arity(); i++){
            environment.define(declaration.params.get(i).lexeme(), arguments.get(i));
        }
        try{
            interpreter.executeBlock(declaration.body, environment);
        }catch (ReturnValue val){
            return val.value;
        }
        if (isInitializer) return closure.getAt("this", 0);
        return null;
    }

    @Override
    public boolean isWrapped() {
        return isWrapped;
    }


    @Override
    public String toString() {
        return "function##" + declaration.name.lexeme();
    }

    public GFunction bind(GClassInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new GFunction(declaration, environment, true );
    }

    public Stmt.Function getDeclaration() {
        return declaration;
    }
}
