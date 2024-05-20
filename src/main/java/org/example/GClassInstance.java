package org.example;

import java.util.ArrayList;
import java.util.HashMap;

public class GClassInstance {
    private final GClass clazz;
    private final HashMap<String, Object> fields = new HashMap<>();

    public GClassInstance(GClass clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "instance##" + clazz.getName();
    }

    Object get(Token name) {
        if (fields.containsKey(name.getLexeme())) {
            return fields.get(name.getLexeme());
        }

        GFunction method = clazz.findMethod(name.getLexeme());
        if(method != null) return method.bind(this);

        App.runtimeError(name,"Undefined property '" + name.getLexeme() + "'");
        return null;
    }

    void set(Token name, Object value) {
        fields.put(name.getLexeme(), value);
    }
}
