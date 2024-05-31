package org.example;

import java.util.HashMap;

public class GClassInstance {
    private final GClass clazz;
    private final HashMap<String, Object> fields = new HashMap<>();

    public GClassInstance(GClass clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "instance##" + clazz.name();
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme())) {
            return fields.get(name.lexeme());
        }

        GFunction method = clazz.findMethod(name.lexeme());
        if(method != null) return method.bind(this);

        App.runtimeError(name,"Undefined property '" + name.lexeme() + "'");
        return null;
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme(), value);
    }
}
