package org.example;

import java.util.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
    private final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();
    public final Map<String, GClass> wrapperClasses = new HashMap<>();
    public Interpreter() {
        StandardLibCreator.defineStandardLib(globals, this);
    }

    void interpret(List<Stmt> statements) {
        for (Stmt statement : statements) {
                execute(statement);
            }
    }

    private String stringify(Object object) {
        if (object == null) return "null";
        if(object.getClass().isArray()){
            Object[] arrayValues = (Object[])object;
            StringBuilder s = new StringBuilder("[");
            int i = 0;
            for(Object val : arrayValues){
                s.append(stringify(val));
                if(i < arrayValues.length-1) s.append(", ");
                i++;
            }
            return s + "]";
        }
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    @Override
    public Object visitAdditionAssignExpr(Expr.AdditionAssign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);

        Object oldValue = environment.get(expr.name);
        Object newValue = plusEq(value, oldValue, expr.name);

        if (distance != null) {
            environment.assignAt(distance, expr.name, newValue);
        } else {
            globals.assign(expr.name, newValue);
        }

        return newValue;
    }

    @Override
    public Object visitArrayAssignExpr(Expr.ArrayAssign expr) {
        Object value = evaluate(expr.value);
        Expr.Postfix post = (Expr.Postfix)expr.postfix;

        int i = getArrayAssignIndex(post);

        Object v = evaluate(post.left);
        if(!v.getClass().isArray()) App.runtimeError(post.operator, "Cannot use array access operator on this");
        Object[] arr = (Object[]) v;
        arr[i] = value;
        return arr;
    }

    private int getArrayAssignIndex(Expr.Postfix post) {
        Object index = evaluate(post.val);
        if(!(index instanceof Double ind)) {
            App.runtimeError(post.operator, "Expected numeric index");
            return -1;
        }

        if(ind % 1 != 0) App.runtimeError(post.operator, "Expected integer index");
        return (int) (double)ind;
    }

    @Override
    public Object visitAdditionArrayAssignExpr(Expr.AdditionArrayAssign expr) {
        Object value = evaluate(expr.value);
        Expr.Postfix post = (Expr.Postfix)expr.postfix;

        int i = getArrayAssignIndex(post);

        Object v = evaluate(post.left);
        if(!v.getClass().isArray()) App.runtimeError(post.operator, "Cannot use array access operator on this");
        Object[] arr = (Object[]) v;
        arr[i] = addOrConcat(arr[i],  value, expr.eq);
        return arr;
    }

    private Object addOrConcat(Object o1, Object o2, Token operator) {
        if (o1 instanceof Double && o2 instanceof Double) {
            return (double)o1 + (double)o2;
        }
        if (o1 instanceof Double && o2 instanceof String) {
            return stringify(o1) + o2;
        }
        // Concat strings
        if (o1 instanceof String && o2 instanceof String) {
            return o1 + (String)o2;
        }

        if (o1 instanceof String && o2 instanceof Double) {
            return o1 + stringify(o2);
        }
        App.runtimeError(operator, "Expected numbers, or strings. Got: " + o1.getClass().getSimpleName() + "  +  " + o2.getClass().getSimpleName());
        return null;
    }

    private Object plusEq(Object value, Object oldValue, Token name) {
        Object newValue = null;

        if((oldValue instanceof Double || oldValue instanceof String) && (value instanceof Double || value instanceof String)){
            if(oldValue instanceof Double){
                if(value instanceof Double) {
                    newValue = (Double) oldValue + (Double) value;
                }else{
                    newValue = stringify(oldValue) + value;
                }
            }else{
                newValue = stringify(oldValue) + stringify(value);
            }
        }else{
            App.runtimeError(name, "Cannot use '+=' on these types");
        }
        return newValue;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        return switch (expr.operator.tokenType()) {
            case MINUS -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left - (double) right;
            }
            case SLASH -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left / (double) right;
            }
            case PERCENT -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left % (double) right;
            }
            case STAR -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left * (double) right;
            }
            case PLUS ->
                // Add numbers
                    addOrConcat(left, right, expr.operator);
            case GREATER -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left <= (double) right;
            }
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            default -> null;
        };
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object obj = evaluate(expr.object);
        if(obj == null){
            App.runtimeError(expr.name, "Null ptrs do not have properties");
            return null;
        }
        if(!(obj instanceof GClassInstance)){
            App.runtimeError(expr.name, "Only objects have properties");
            return null;
        }


        return ((GClassInstance)obj).get(expr.name);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object obj = evaluate(expr.object);
        if(!(obj instanceof GClassInstance)){
            App.runtimeError(expr.name, "Only objects have fields");
            return null;
        }

        Object val = evaluate(expr.value);
        ((GClassInstance)obj).set(expr.name, val);
        return val;
    }

    @Override
    public Object visitAdditionSetExpr(Expr.AdditionSet expr) {
        Object obj = evaluate(expr.object);
        if(!(obj instanceof GClassInstance)){
            App.runtimeError(expr.name, "Only objects have fields");
            return null;
        }

        Object value = evaluate(expr.value);

        Object oldValue = ((GClassInstance)obj).get(expr.name);
        Object newValue = plusEq(value, oldValue, expr.name);


        ((GClassInstance)obj).set(expr.name, newValue);
        return newValue;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for(Expr arg : expr.arguments){
            arguments.add(evaluate(arg));
        }

        if(!(callee instanceof GCallable function)){
            App.runtimeError(expr.paren, "Cannot call this.");
            return null;
        }

        if(function.isWrapped() && environment.isDeclaredHere("__END_WRAPPER_DECL__")){
            App.runtimeError(expr.paren, "Cannot call this.");
        }

        if (arguments.size() != function.arity()) {
            App.runtimeError(expr.paren, "Expected " +
                    function.arity() + " argument" + (function.arity() > 1 ? "s" : "") + " but got " +
                    (arguments.isEmpty() ? "none" : arguments.size()) + ".");
           return null;
        }
        return function.call(this, arguments);
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        App.runtimeError(operator, "Operands must be numbers.");
    }

     private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        App.runtimeError(operator, "Operand must be a number.");
     }

    private boolean isEqual(Object left, Object right) {
        if(left == null && right == null) return true;
        if(left == null || right == null) return false;
        return left.equals(right);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitArrayInitExpr(Expr.ArrayInit expr) {
        List<Object> values = new ArrayList<>();
        for(Expr e : expr.exprs){
            values.add(evaluate(e));
        }
        Object[] obj = new Object[values.size()];
        for(int i = 0; i < values.size(); i++){
            obj[i] = values.get(i);
        }

        return obj;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        switch (expr.operator.tokenType()){
            case OR -> {
                if (isTruthy(left)) return left;
                return evaluate(expr.right);
            }
            case XOR -> {
                boolean l = isTruthy(left);
                boolean r = isTruthy(evaluate(expr.right));
                if((l || r) && (!(l && r))) return true;
                return false;
            }
            case AND -> {
                if (!isTruthy(left)) return left;
                return evaluate(expr.right);
            }
        }

        return null;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object value = evaluate(expr.right);
        switch (expr.operator.tokenType()) {
            case MINUS -> {
                checkNumberOperand(expr.operator, value);
                return -(double) value;
            }
            case BANG -> {
                return !isTruthy(value);
            }
            case PLUS_PLUS, MINUS_MINUS -> {
                if(!(expr.right instanceof Expr.Variable)){
                    App.runtimeError(expr.operator, "Expected name of variable");
                }
                Expr.Variable varexpr = (Expr.Variable) expr.right;
                Object var = lookUpVariable(varexpr.name, varexpr);
                checkNumberOperand(expr.operator, value);

                value = ((double)value) + (expr.operator.tokenType() == TokenType.PLUS_PLUS ? 1 : -1);

                Integer distance = locals.get(varexpr);
                if (distance != null) {
                    environment.assignAt(distance, varexpr.name, value);
                } else {
                    globals.assign(varexpr.name, value);
                }
                return value;
            }
        }

        return null;
    }

    @Override
    public Object visitTernaryExpr(Expr.Ternary expr) {
        Object eval = evaluate(expr.cond);
        if(isTruthy(eval)){
            return evaluate(expr.exp1);
        }else{
            return evaluate(expr.exp2);
        }
    }

    @Override
    public Object visitPostfixExpr(Expr.Postfix expr) {
        Object value = evaluate(expr.left);
        if(expr.operator.tokenType() != TokenType.LEFT_BRACKET) {
            if (!(expr.left instanceof Expr.Variable)) {
                App.runtimeError(expr.operator, "Expected name of variable");
                return null;
            }
            Expr.Variable varexpr = (Expr.Variable) expr.left;
            Object var = lookUpVariable(varexpr.name, varexpr);
            checkNumberOperand(expr.operator, value);

            Object newvalue = ((double) value) + (expr.operator.tokenType() == TokenType.PLUS_PLUS ? 1 : -1);

            Integer distance = locals.get(varexpr);
            if (distance != null) {
                environment.assignAt(distance, varexpr.name, newvalue);
            } else {
                globals.assign(varexpr.name, newvalue);
            }
            return value;
        }

        if(!value.getClass().isArray()) App.runtimeError(expr.operator, "Cannot use array access operator on this");

        int i = getArrayAssignIndex(expr);

        return ((Object[]) value)[i];
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        GClass base = (GClass) environment.getAt("super", distance);

        GClassInstance object = (GClassInstance)environment.getAt("this", distance - 1);

        GFunction method = base.findMethod(expr.method.lexeme());

        if(method == null){
            App.runtimeError(expr.method, "Undefined property " + expr.method.lexeme());
        }


        // Define 'this' as child object
        return method.bind(object);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(name.lexeme(), distance);
        } else {
            return globals.get(name);
        }
    }


    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        if (object instanceof Double) return (double)object != 0;
        return true;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    public void executeBlock(List<Stmt> statements, Environment enviornment) {
        Environment parent = this.environment;
        try {
            this.environment = enviornment;

            statements.forEach(this::execute);

        } finally {
            this.environment = parent;
        }
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        GFunction func = new GFunction(stmt, environment, false, false);
        environment.define(stmt.name.lexeme(), func);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if(isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        }else if(stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        try {
            while(isTruthy(evaluate(stmt.condition))){
                execute(stmt.body);
            }
        }catch (BreakFrom ignored){

        }

        return null;
    }

    @Override
    public Void visitForEachStmt(Stmt.ForEach stmt) {

        Object iterable = evaluate(stmt.iterable);
        List<Object> items = new ArrayList<>();

        GClass wrappedList = wrapperClasses.get("List");
        GClass wrappedMap = wrapperClasses.get("Dict");
        GClass wrappedPair = wrapperClasses.get("Pair");

        // Check to make sure that iterable is actually iterable
        if((!iterable.getClass().isArray())
                && (!(iterable instanceof List))
                && (!(iterable instanceof GClassInstance))
                && (!(iterable instanceof Map))){
            App.runtimeError(stmt.colon, "Expected iterable object here");
        }


        if(iterable.getClass().isArray()){
            items.addAll(List.of((Object[]) iterable));
        }else if(iterable instanceof List<?> iterableList){
            items.addAll(iterableList);
        }else if(iterable instanceof GClassInstance iterableClassInstance){
            if(iterableClassInstance.getGClass().equals(wrappedList)){
                Object arrObj = iterableClassInstance.getField("arr");
                if(arrObj instanceof List<?> vals) {
                    items.addAll(vals);
                }
            }else if(iterableClassInstance.getGClass().equals(wrappedMap)){
                if(iterableClassInstance.getField("map") instanceof Map<?,?> vals) {
                    for (Map.Entry<?, ?> entry : vals.entrySet()) {
                        GClassInstance pair = (GClassInstance) wrappedPair.call(this, List.of(entry.getKey(), entry.getValue()));
                        items.add(pair);
                    }
                }
            }else{
                App.runtimeError(stmt.colon, "Expected iterable object here");
            }
        }


        try {
        //Environment parent = environment;
        //environment = new Environment(parent);



            for (Object o : items) {

                environment.define(stmt.var.lexeme(), o);
                execute(stmt.body);
            }
            //environment = parent;
        }catch (BreakFrom ignored){}


        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.print(stringify(value));
        return null;
    }


    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if(stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme(), value);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;

        if(stmt.value != null) {
            value = evaluate(stmt.value);
        }
        throw new ReturnValue(value);
    }

    @Override
    public Void visitSwitchStmt(Stmt.Switch stmt) {
        Object switching = evaluate(stmt.expression);
        boolean hasMatched = false;
        try {
            for (int i = 0; i < stmt.caseValues.size(); i++) {
                Object o = evaluate(stmt.caseValues.get(i));
                if (o.equals(switching)) {
                    execute(stmt.caseBodies.get(i));
                    hasMatched = true;
                }
            }

            if (stmt.defaultCase != null && !hasMatched) {
                execute(stmt.defaultCase);
            }
        } catch (BreakFrom ignored) {}
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new BreakFrom();
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object base;
        GClass baseclass = null;
        if (stmt.base != null) {
            base = evaluate(stmt.base);
            if (!(base instanceof GClass)) {
                App.runtimeError(stmt.base.name, "Superclass must be a class.");
            }else{
                baseclass = (GClass) base;
            }
        }

        environment.define(stmt.name.lexeme(), null);

        if(stmt.base != null){
            environment = new Environment(environment);
            environment.define("super", baseclass);
        }

        HashMap<String, GFunction> methods = new HashMap<>();
        for(Stmt.Function method : stmt.methods){
            GFunction func = new GFunction(method, environment, method.name.lexeme().equals("constructor"), false);
            methods.put(method.name.lexeme(), func);
        }


        GClass clazz = new GClass(stmt.name.lexeme(), methods, baseclass);
        if(!environment.isDeclaredHere("__END_WRAPPER_DECL__")){
            wrapperClasses.put(clazz.name(), clazz);
        }
        if(stmt.base != null){
            environment = environment.getParent();
        }
        environment.assign(stmt.name, clazz);

        return null;
    }

    public void resolve(Expr expr, int i) {
        locals.put(expr, i);
    }
}
