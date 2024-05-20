package org.example;

import java.util.HashMap;

public class Environment {
    private final Environment parent;
    /**
     * Represents variables, and functions which are of type {@code GCallable}
     *
     * @see GCallable
     */
    private final HashMap<String, Object> variables = new HashMap<>();

    public Environment() {
        this.parent = null;
    }

    /**
     * Creates new Environment
     *
     * @param parent the parent of this scope or {@code null} if global scope
     */
    public Environment(Environment parent) {
        this.parent = parent;
    }

    /**
     * Define a new variable in this scope
     * It may shadow one in an outer scope
     *
     * @param name  the name of the variable
     * @param value its value or {@code null} if left uninitialized
     */
    public void define(String name, Object value) {
        variables.put(name, value);
    }

    /**
     * Assign value to variable
     *
     * @param name  the name of the variable
     * @param value the value to be assigned
     * @throws RuntimeError if variable is undefined
     */
    public void assign(Token name, Object value) {
        if (variables.containsKey(name.getLexeme())) {
            variables.put(name.getLexeme(), value);
            return;
        }
        if (parent != null) {
            parent.assign(name, value);
            return;
        }
        App.runtimeError(name, "Undefined variable " + name.getLexeme());
    }

    /**
     * Assign value to variable in a distant scope
     *
     * @param name     the name of the variable
     * @param value    the value to be assigned
     * @param distance how many levels up
     * @throws RuntimeError if variable is undefined
     */
    public void assignAt(int distance, Token name, Object value) {
        getDistParent(distance).variables.put(name.getLexeme(), value);
    }

    /**
     * Get the value of the variable
     *
     * @param name the name of the variable
     * @return the value of the variable
     * @throws RuntimeError if variable is undefined
     */
    public Object get(Token name) {
        if (variables.containsKey(name.getLexeme())) {
            return variables.get(name.getLexeme());
        } else {
            if (parent != null) return parent.get(name);
        }
        App.runtimeError(name, "Undefined variable " + name.getLexeme());
        return null;
    }



    /**
     * Get the value of the variable in a distant scope
     *
     * @param name     the name of the variable
     * @param distance how many levels up
     * @return the value of the variable
     * @throws RuntimeError if variable is undefined
     */
    public Object getAt(String name, Integer distance) {
        return getDistParent(distance).variables.get(name);
    }

    /**
     * Returns an {@code Environment} above this one
     *
     * @param distance how many levels up
     * @return a parent {@code Environment}
     */
    private Environment getDistParent(Integer distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.parent;
        }
        return environment;

    }

    public Environment getParent() {
        return parent;
    }
}