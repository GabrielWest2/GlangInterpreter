package org.example.tools;

import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

public class WrapperGenerator {
    public static void main(String[] args) throws NoSuchMethodException {
        new WrapperGenerator();
    }

    public WrapperGenerator() {
        Reflections reflections = new Reflections("org.example",
                new SubTypesScanner(false),
                new MethodParameterScanner());

        Set<Class<?extends Object>> classes =  reflections.getSubTypesOf(Object.class);

        for(Class<?extends Object> clazz : classes){
            if(clazz.isAnonymousClass()) continue;
            System.out.println(clazz.getSimpleName());


            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for(Constructor cons : constructors){
                System.out.println("    constructor " + cons.getParameters().length);
            }

            Method[] methods = clazz.getDeclaredMethods();
            for(Method m : methods){
                System.out.print("    method " + m.getName() + " ( ");
                for(Parameter p : m.getParameters())
                {
                    System.out.print(p.getType().getSimpleName() + " " + p.getName() + ",");
                }
            }
        }

    }



}
