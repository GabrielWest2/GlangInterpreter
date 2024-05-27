package org.example;

import java.util.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
    private final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    public Interpreter() {
        StandardLibCreator.defineStandardLib(globals);
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            error.printStackTrace();
        }
    }

    private String stringify(Object object) {
        if (object == null) return "null";
        if(object.getClass().isArray()){
            Object[] arrayValues = (Object[])object;
            String s = "[";
            int i = 0;
            for(Object val : arrayValues){
                s += stringify(val);
                if(i < arrayValues.length-1) s += ", ";
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
        Object newValue = plusEq(value, oldValue, expr.name, expr);

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

        Object index = evaluate(post.val);
        if(!(index instanceof Double)) App.runtimeError(post.operator, "Expected numeric index");
        Double ind = (Double) index;
        if(ind % 1 != 0) App.runtimeError(post.operator, "Expected integer index");
        int i = (int) (double)ind;

        Object[] arr = (Object[]) evaluate(post.left);
        arr[i] = value;
        return arr;
    }

    private Object plusEq(Object value, Object oldValue, Token name, Expr expr) {
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
        switch (expr.operator.getTokenType()){
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case PERCENT:
                checkNumberOperands(expr.operator, left, right);
                return (double)left % (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case PLUS:
                // Add numbers
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof Double && right instanceof String) {
                    return stringify(left) + right;
                }
                // Concat strings
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                if (left instanceof String && right instanceof Double) {
                    return (String)left + stringify(right);
                }
                App.runtimeError(expr.operator, "Expected numbers, or strings.");
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
        }
        return null;
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object obj = evaluate(expr.object);
        if(obj == null) App.runtimeError(expr.name, "Null ptrs do not have properties");
        if(!(obj instanceof GClassInstance)) App.runtimeError(expr.name, "Only objects have properties");

        return ((GClassInstance)obj).get(expr.name);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object obj = evaluate(expr.object);
        if(!(obj instanceof GClassInstance)) App.runtimeError(expr.name, "Only objects have fields");

        Object val = evaluate(expr.value);
        ((GClassInstance)obj).set(expr.name, val);
        return val;
    }

    @Override
    public Object visitAdditionSetExpr(Expr.AdditionSet expr) {
        Object obj = evaluate(expr.object);
        if(!(obj instanceof GClassInstance)) App.runtimeError(expr.name, "Only objects have fields");

        Object value = evaluate(expr.value);

        System.out.println("rb");
        Object oldValue = ((GClassInstance)obj).get(expr.name);
        Object newValue = plusEq(value, oldValue, expr.name, expr);


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

        if(!(callee instanceof GCallable)){
            App.runtimeError(expr.paren, "Cannot call this.");
        }

        GCallable function = (GCallable) callee;
        if (arguments.size() != function.arity()) {
            App.runtimeError(expr.paren, "Expected " +
                    function.arity() + " argument" + (function.arity() > 1 ? "s" : "") + " but got " +
                    (arguments.size() == 0 ? "none" : arguments.size()) + ".");
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
        switch (expr.operator.getTokenType()){
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
        if (expr.operator.getTokenType() == TokenType.OR) {

        } else {

        }

        return null;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object value = evaluate(expr.right);
        switch (expr.operator.getTokenType()) {
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

                value = ((double)value) + (expr.operator.getTokenType() == TokenType.PLUS_PLUS ? 1 : -1);

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
        if(expr.operator.getTokenType() != TokenType.LEFT_BRACKET) {
            if (!(expr.left instanceof Expr.Variable)) {
                App.runtimeError(expr.operator, "Expected name of variable");
                return null;
            }
            Expr.Variable varexpr = (Expr.Variable) expr.left;
            Object var = lookUpVariable(varexpr.name, varexpr);
            checkNumberOperand(expr.operator, value);

            Object newvalue = ((double) value) + (expr.operator.getTokenType() == TokenType.PLUS_PLUS ? 1 : -1);

            Integer distance = locals.get(varexpr);
            if (distance != null) {
                environment.assignAt(distance, varexpr.name, newvalue);
            } else {
                globals.assign(varexpr.name, newvalue);
            }
            return value;
        }

        if(!value.getClass().isArray()) App.runtimeError(expr.operator, "Cannot use array access operator on this object");

        Object index = evaluate(expr.val);
        if(!(index instanceof Double)) App.runtimeError(expr.operator, "Expected numeric index");
        Double ind = (Double) index;
        if(ind % 1 != 0) App.runtimeError(expr.operator, "Expected integer index");
        int i = (int) (double)ind;

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

        GFunction method = base.findMethod(expr.method.getLexeme());

        if(method == null){
            App.runtimeError(expr.method, "Undefined property " + expr.method.getLexeme());
        }


        // Define 'this' as child object
        return method.bind(object);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(name.getLexeme(), distance);
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
        GFunction func = new GFunction(stmt, environment, false);
        environment.define(stmt.name.getLexeme(), func);
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
        while(isTruthy(evaluate(stmt.condition))){
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }


    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if(stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.getLexeme(), value);
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
    public Void visitClassStmt(Stmt.Class stmt) {
        Object base = null;
        GClass baseclass = null;
        if (stmt.base != null) {
            base = evaluate(stmt.base);
            if (!(base instanceof GClass)) {
                App.runtimeError(stmt.base.name, "Superclass must be a class.");
            }else{
                baseclass = (GClass) base;
            }
        }

        environment.define(stmt.name.getLexeme(), null);

        if(stmt.base != null){
            environment = new Environment(environment);
            environment.define("super", baseclass);
        }

        HashMap<String, GFunction> methods = new HashMap<>();
        for(Stmt.Function method : stmt.methods){
            GFunction func = new GFunction(method, environment, method.name.getLexeme().equals("constructor"));
            methods.put(method.name.getLexeme(), func);
        }


        GClass clazz = new GClass(stmt.name.getLexeme(), methods, baseclass);

        if(stmt.base != null){
            environment = environment.getParent();
        }
        environment.assign(stmt.name, clazz);

        return null;
    }
    public Environment getGlobals() {
        return globals;
    }

    public void resolve(Expr expr, int i) {
        locals.put(expr, i);
    }
}
