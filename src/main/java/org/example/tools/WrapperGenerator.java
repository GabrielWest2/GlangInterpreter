/*
package org.example.tools;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.HashSet;
import java.util.Set;

import static org.reflections.scanners.Scanners.*;

public class WrapperGenerator {
    public static void main(String[] args) {

        Reflections reflections = new Reflections("org.example", new SubTypesScanner(false));
        Set<Class<?>> classes = new HashSet<>(reflections.getSubTypesOf(Object.class));

        for(Class<?> clazz : classes) {
            if(clazz.getSimpleName().isBlank()) continue;
            System.out.println(clazz.getSimpleName());
            //Set<Method> methods = reflections.
            //for(Method m : methods){
            //    System.out.println("  " + m.getName());
            //}
        }
    }
}
*/
