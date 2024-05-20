package org.example;

import java.util.HashMap;
import java.util.List;

public class GClass implements GCallable {
    private final String name;
    private final HashMap<String, GFunction> methods;
    private final GClass baseclass;

    public GClass(String name, HashMap<String, GFunction> methods, GClass baseclass) {
        this.name = name;
        this.methods = methods;
        this.baseclass = baseclass;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "class##" + name;
    }

    @Override
    public int arity() {
        GFunction initializer = findMethod("constructor");
        if (initializer != null) {
            return initializer.arity();
        }
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        GClassInstance instance = new GClassInstance(this);
        GFunction initializer = findMethod("constructor");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    GFunction findMethod(String name) {
         if (methods.containsKey(name)) {
            return methods.get(name);
         }
        if (baseclass != null) {
            return baseclass.findMethod(name);
        }
         return null;
     }

    public HashMap<String, GFunction> getMethods() {
        return methods;
    }

    public GClass getBaseclass() {
        return baseclass;
    }
}
