package org.example;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * Returns the names of all variable names in this scope and above
     * @param includeGlobals include global scope
     * @return {@code List<String>} of the names
     */
    public List<String> getAllVariablesNames(boolean includeGlobals){
        List<String> names = new ArrayList<>();
        if(parent == null && !includeGlobals) return names;

        names.addAll(variables.keySet());
        if(parent != null){
            names.addAll(parent.getAllVariablesNames(includeGlobals));
        }
        return names;
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

        recommendFix(name);
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
        recommendFix(name);
        return null;
    }

    private void recommendFix(Token name) {
        List<String> names = getAllVariablesNames(false);
        List<ExtractedResult> results = FuzzySearch.extractAll(name.getLexeme(), names);
        int highestId = 0;
        int highestScore = 0;
        for(ExtractedResult r : results){
            if(r.getScore() > highestScore){
                highestScore = r.getScore();
                highestId = r.getIndex();
            }
        }
        String ex = "";
        if(highestScore > 75){
            ex = "\n  Did you mean '"+names.get(highestId)+"'?\nscore:"+highestScore;
        }
        App.runtimeError(name, "Undefined variable '" + name.getLexeme()+"'" + ex);
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