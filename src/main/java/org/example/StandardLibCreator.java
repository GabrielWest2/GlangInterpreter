package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

/**
 * Contains default functions and constants
 */
public class StandardLibCreator {
    /**
     * Defines all standard library constants and functions
     * @param globals
     * @param interpreter
     */
    public static void defineStandardLib(Environment globals, Interpreter interpreter) {
        Scanner scanner = new Scanner(System.in);
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

        globals.define("println", new GCallable() {
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

        globals.define("scanNum", new GCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return scanner.nextDouble();
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });


        globals.define("scanLine", new GCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return scanner.nextLine();
            }

            @Override
            public boolean isWrapped() {
                return false;
            }
        });

        globals.define("toNum", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return Double.parseDouble(arguments.get(0).toString());
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
        globals.define("_getKeysHashmap", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                HashMap<Object, Object> map = (HashMap<Object, Object>)arguments.get(0);
                return new ArrayList<>(map.keySet());
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_getValuesHashmap", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                HashMap<Object, Object> map = (HashMap<Object, Object>)arguments.get(0);
                return new ArrayList<>(map.values());
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_getPairsHashmap", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                HashMap<Object, Object> map = (HashMap<Object, Object>)arguments.get(0);
                List<Object> pairs = new ArrayList<>();
                GClass wrappedPair = interpreter.wrapperClasses.get("Pair");
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    GClassInstance pair = (GClassInstance) wrappedPair.call(interpreter, List.of(entry.getKey(), entry.getValue()));
                    pairs.add(pair);
                }
                return pairs.toArray();
            }
            @Override
            public boolean isWrapped() {
                return true;
            }
        });


        globals.define("_setGraphicsColor", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);
                GClassInstance color = (GClassInstance) arguments.get(1);
                Color co = new Color(
                        (int) ((Double) color.getField("r")).doubleValue(),
                        (int) ((Double) color.getField("g")).doubleValue(),
                        (int) ((Double) color.getField("b")).doubleValue(),
                        (int) ((Double) color.getField("a")).doubleValue()
                );
                g.setColor(co);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_setGraphicsFont", new GCallable() {
            @Override
            public int arity() {
                return 4;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);
                String name = (String) arguments.get(1);
                int style = (int) ((Double) arguments.get(2)).doubleValue();
                int weight = (int) ((Double) arguments.get(3)).doubleValue();

                Font f = new Font(name, style, weight);
                g.setFont(f);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_drawLineGraphics", new GCallable() {
            @Override
            public int arity() {
                return 5;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int x2 = (int) ((Double) arguments.get(3)).doubleValue();
                int y2 = (int) ((Double) arguments.get(4)).doubleValue();

                g.drawLine(x1, y1, x2, y2);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_drawRectGraphics", new GCallable() {
            @Override
            public int arity() {
                return 5;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int w = (int) ((Double) arguments.get(3)).doubleValue();
                int h = (int) ((Double) arguments.get(4)).doubleValue();

                g.drawRect(x1, y1, w, h);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_drawRoundRectGraphics", new GCallable() {
            @Override
            public int arity() {
                return 7;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics2D g = (Graphics2D) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int w = (int) ((Double) arguments.get(3)).doubleValue();
                int h = (int) ((Double) arguments.get(4)).doubleValue();
                int aw = (int) ((Double) arguments.get(5)).doubleValue();
                int ah = (int) ((Double) arguments.get(6)).doubleValue();

                g.drawRoundRect(x1, y1, w, h, aw, ah);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_drawOvalGraphics", new GCallable() {
            @Override
            public int arity() {
                return 5;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int w = (int) ((Double) arguments.get(3)).doubleValue();
                int h = (int) ((Double) arguments.get(4)).doubleValue();

                g.drawOval(x1, y1, w, h);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_drawArcGraphics", new GCallable() {
            @Override
            public int arity() {
                return 7;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int w = (int) ((Double) arguments.get(3)).doubleValue();
                int h = (int) ((Double) arguments.get(4)).doubleValue();
                int angleStart = (int) ((Double) arguments.get(5)).doubleValue();
                int angleExtent = (int) ((Double) arguments.get(6)).doubleValue();

                g.drawArc(x1, y1, w, h, angleStart, angleExtent);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_drawStringGraphics", new GCallable() {
            @Override
            public int arity() {
                return 4;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics2D g = (Graphics2D) arguments.get(0);

                String str = (String) arguments.get(1);
                int x1 = (int) ((Double) arguments.get(2)).doubleValue();
                int y1 = (int) ((Double) arguments.get(3)).doubleValue();


                g.drawString(str, x1, y1);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_fillRectGraphics", new GCallable() {
            @Override
            public int arity() {
                return 5;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int w = (int) ((Double) arguments.get(3)).doubleValue();
                int h = (int) ((Double) arguments.get(4)).doubleValue();

                g.fillRect(x1, y1, w, h);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_fillRoundRectGraphics", new GCallable() {
            @Override
            public int arity() {
                return 7;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics2D g = (Graphics2D) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int w = (int) ((Double) arguments.get(3)).doubleValue();
                int h = (int) ((Double) arguments.get(4)).doubleValue();
                int aw = (int) ((Double) arguments.get(5)).doubleValue();
                int ah = (int) ((Double) arguments.get(6)).doubleValue();

                g.fillRoundRect(x1, y1, w, h, aw, ah);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });
        globals.define("_fillOvalGraphics", new GCallable() {
            @Override
            public int arity() {
                return 5;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int w = (int) ((Double) arguments.get(3)).doubleValue();
                int h = (int) ((Double) arguments.get(4)).doubleValue();

                g.fillOval(x1, y1, w, h);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_fillArcGraphics", new GCallable() {
            @Override
            public int arity() {
                return 7;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Graphics g = (Graphics) arguments.get(0);

                int x1 = (int) ((Double) arguments.get(1)).doubleValue();
                int y1 = (int) ((Double) arguments.get(2)).doubleValue();
                int w = (int) ((Double) arguments.get(3)).doubleValue();
                int h = (int) ((Double) arguments.get(4)).doubleValue();
                int angleStart = (int) ((Double) arguments.get(5)).doubleValue();
                int angleExtent = (int) ((Double) arguments.get(6)).doubleValue();

                g.fillArc(x1, y1, w, h, angleStart, angleExtent);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });













        globals.define("_canvasCreate", new GCallable() {
            @Override
            public int arity() {
                return 3;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                String title = (String) arguments.get(0);
                int width = (int) ((Double) arguments.get(1)).doubleValue();
                int height = (int) ((Double) arguments.get(2)).doubleValue();

                GabeFrame frame = new GabeFrame(title, width, height, interpreter);
                return frame;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_canvasSetVisible", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                JFrame frame = (JFrame) arguments.get(0);
                Boolean shown = (Boolean) arguments.get(1);

                frame.setVisible(shown);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("_canvasDispose", new GCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                JFrame frame = (JFrame) arguments.get(0);
                frame.dispose();
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });


        globals.define("_canvasSetCallback", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                GabeFrame frame = (GabeFrame) arguments.get(0);
                GCallable callable = (GCallable) arguments.get(1);

                frame.setPaintMethod(callable);
                return null;
            }

            @Override
            public boolean isWrapped() {
                return true;
            }
        });

        globals.define("range", new GCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                int arg1 = (int)((Double)arguments.get(0)).doubleValue();
                int arg2 = (int)((Double)arguments.get(1)).doubleValue();

                Object[] nums = new Object[arg2 - arg1];
                int i = 0;
                for(double d = (double) arg1; d < arg2; d += 1.0){
                    nums[i++] = d;
                }
                return nums;
            }

            @Override
            public boolean isWrapped() {
                return false;
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
