package org.example;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a class, and stores it methods <br/>
 * Implements {@code GCallable}, so it can be called
 * as such: {@code ... = ClassName()}, calling the constructor of the class
 * @param name the name of the class
 * @param methods a list of the classes methods as {@code GFunction}s
 * @param baseclass the base {@code GClass} or {@code null} if there
 *                  is no derived class
 * @see GCallable
 * @see GFunction
 */
public record GClass(String name, HashMap<String, GFunction> methods,
                     org.example.GClass baseclass) implements GCallable {

    @Override
    public String toString() {
        return "class##" + name;
    }

    /**
     * Get the arity of this callable object
     * @return the number of parameters required
     */
    @Override
    public int arity() {
        GFunction initializer = findMethod("constructor");
        if (initializer != null) {
            return initializer.arity();
        }
        return 0;
    }

    /**
     * Call this callable object
     * @param interpreter the {@code Interpreter} instance
     * @param arguments list of arguments
     * @return return value or {@code null} if none
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        GClassInstance instance = new GClassInstance(this);
        GFunction initializer = findMethod("constructor");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public boolean isWrapped() {
        return false;
    }

    /**
     * Finds a method by its name
     * @param name the name of the method
     * @return the method as a {@code GCallable}
     */
    GFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }
        if (baseclass != null) {
            return baseclass.findMethod(name);
        }
        return null;
    }
}
