package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class StandardLibCreator {
    public static void defineStandardLib(Environment globals) {
        globals.define("PI", Math.PI);

        globals.define("clock", new GCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return  ((double)System.currentTimeMillis()) / 1000.0d;
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("print", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                System.out.println(arguments.get(0));
                return null;
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("exit", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if(arguments.get(0) instanceof Double){
                    double d = (Double) arguments.get(0);
                    System.exit((int)d);

                }
                return null;
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });
        globals.define("_createArrayList", new GCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return new ArrayList<Object>();
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_getArrayListItem", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((ArrayList<Object>)arguments.get(0)).get((int)((Double)arguments.get(1)).doubleValue());
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_addArrayListItem", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((ArrayList<Object>)arguments.get(0)).add(arguments.get(1));
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_addArrayListItemAt", new GCallable() {
            @Override
            public int arity() {
                return 3;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                ((ArrayList<Object>)arguments.get(0)).add((int)((Double)arguments.get(1)).doubleValue(), arguments.get(2));
                return null ;
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_setArrayListItem", new GCallable() {
            @Override
            public int arity() {
                return 3;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((ArrayList<Object>)arguments.get(0)).set((int)((Double)arguments.get(1)).doubleValue(), arguments.get(2));
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_getArrayListLength", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)((ArrayList<Object>)arguments.get(0)).size();
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_createHashMap", new GCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return new HashMap<Object, Object>();
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_putHashMap", new GCallable() {
            @Override
            public int arity() {
                return 3;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((HashMap<Object, Object>)arguments.get(0)).put(arguments.get(1), arguments.get(2));
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_getHashMap", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((HashMap<Object, Object>)arguments.get(0)).get(arguments.get(1));
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_containsHashMapKey", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((HashMap<Object, Object>)arguments.get(0)).containsKey(arguments.get(1));
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_containsHashMapValue", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((HashMap<Object, Object>)arguments.get(0)).containsValue(arguments.get(1));
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_removeHashMapValue", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((HashMap<Object, Object>)arguments.get(0)).remove(arguments.get(1));
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("len", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Object arg1 = arguments.get(0);
                if(arg1 instanceof String){
                    return (double)((String)arg1).length();
                }
                if(arg1.getClass().isArray()){
                    return (double)((Object[])arg1).length;
                }
                System.out.println("BAD BAD");
                return null;
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("max", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                double arg2 = (double) arguments.get(1);

                return Math.max(arg1, arg2);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });
        globals.define("min", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                double arg2 = (double) arguments.get(1);

                return Math.min(arg1, arg2);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });
        globals.define("sqrt", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.sqrt(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });
        globals.define("abs", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.abs(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("random", new GCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return Math.random();
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("acos", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.acos(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("asin", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.asin(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("sin", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.sin(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("atan", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.atan(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("atan2", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                double arg2 = (double) arguments.get(1);
                return Math.atan2(arg1, arg2);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("cbrt", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.cbrt(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });
        globals.define("ceil", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.ceil(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("copySign", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                double arg2 = (double) arguments.get(1);
                return Math.copySign(arg1, arg2);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("cos", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.cos(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("cosh", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.cosh(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("exp", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.exp(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("expm1", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.expm1(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("floor", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.floor(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("getExponent", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return (double)Math.getExponent(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("hypot", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                double arg2 = (double) arguments.get(1);
                return Math.hypot(arg1, arg2);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("log", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.log(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("log10", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.log10(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("log1p", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.log1p(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("pow", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                double arg2 = (double) arguments.get(1);
                return Math.pow(arg1, arg2);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("rint", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.rint(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("round", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.round(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("toDeg", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.toDegrees(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("toRad", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                double arg1 = (double) arguments.get(0);
                return Math.toRadians(arg1);
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });
    }

    public static List<Token> getWrapperCode(String wrapperSource) throws FileNotFoundException {
        return new Tokenizer(wrapperSource).tokenize(false);
    }
}
