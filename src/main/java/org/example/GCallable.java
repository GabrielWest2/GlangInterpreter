package org.example;

import java.util.List;

/**
 * Represents a callable function or method
 */
public interface GCallable {
    /**
     * Get the arity of this callable object
     * @return the number of parameters required
     */
    int arity();

    /**
     * Call this callable object
     * @param interpreter
     * @param arguments
     * @return return value or {@code null} if none
     */
    Object call(Interpreter interpreter, List<Object> arguments);

    /**
     * Checks to see if this function is a wrapped function.
     * Wrapped functions should be prefixed with an underscore ['_']
     * @return if the function is native
     */
    boolean isWrapped();
}
