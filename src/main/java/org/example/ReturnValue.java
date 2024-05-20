package org.example;

public class ReturnValue extends RuntimeException{
    final Object value;

    public ReturnValue(Object v){
        super(null, null, false, false);
        this.value = v;
    }
}
