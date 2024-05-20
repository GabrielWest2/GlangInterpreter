package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * First pass over the parsed AST
 * Used to resolve variables beforehand,
 * and to check for various errors
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Stack<HashMap<String, Boolean>> scopes;
    private FunctionType functionType = FunctionType.NONE;
    private ClassType classType = ClassType.NONE;

    private enum FunctionType {
        NONE, FUNCTION, METHOD, CONSTRUCTOR;
    }
    private enum ClassType {
        NONE, CLASS, SUBCLASS;
    }

    private final Interpreter interpreter;

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
        scopes = new Stack<>();
    }


    public void resolve(List<Stmt> stmts){
        for(Stmt stmt : stmts){
            resolve(stmt);
        }
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;
        HashMap<String, Boolean> scope = scopes.peek();
        if(scope.containsKey(name.getLexeme())){
            App.resolveError(name, "Variable already declared in this scope.");
        }
        scope.put(name.getLexeme(), false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        HashMap<String, Boolean> scope = scopes.peek();
        scope.put(name.getLexeme(), true);
    }

    private void beginScope(){
        scopes.push(new HashMap<>());
    }
    private HashMap<String, Boolean> endScope(){
        return scopes.pop();
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }


    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolveFunction(Stmt.Function stmt, FunctionType type) {
        FunctionType outerType = functionType;
        functionType = type;


        beginScope();
        for(Token t : stmt.params){
            declare(t);
            define(t);
        }
        resolve(stmt.body);
        endScope();

        functionType = outerType;
    }




    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitAdditionAssignExpr(Expr.AdditionAssign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.object);
        resolve(expr.value);
        return null;
    }

    @Override
    public Void visitAdditionSetExpr(Expr.AdditionSet expr) {
        resolve(expr.object);
        resolve(expr.value);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {

        resolve(expr.callee);

        for(Expr e : expr.arguments){
            resolve(e);
        }
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitThisExpr(Expr.This expr) {
        if(classType == ClassType.NONE){
            App.resolveError(expr.keyword, "Cannot use 'this' here");
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() &&
                scopes.peek().get(expr.name.getLexeme()) == Boolean.FALSE) {
            App.resolveError(expr.name, "Can't read local variable in its own initializer.");
        }
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        if(classType != ClassType.SUBCLASS){
            App.resolveError(expr.keyword, "Cannot use 'super' here");
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }


    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }



    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if(stmt.elseBranch != null)resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if(stmt.initializer != null){
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if(functionType == FunctionType.NONE){
            App.resolveError(stmt.keyword, "Cannot return from here");
        }

        if (stmt.value != null) {
            if(functionType == FunctionType.CONSTRUCTOR){
                App.resolveError(stmt.keyword, "Cannot return a value here");
            }
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        boolean hasBaseClass = stmt.base != null;
        ClassType outerType = classType;
        classType = hasBaseClass ? ClassType.SUBCLASS : ClassType.CLASS;

        declare(stmt.name);
        define(stmt.name);
        if(hasBaseClass && stmt.base.name.getLexeme().equals(stmt.name.getLexeme())){
            App.resolveError(stmt.name, "Class cannot inherit from itself");
        }

        if(hasBaseClass){
            resolve(stmt.base);

            beginScope();
            scopes.peek().put("super", true);
        }



        beginScope();
        scopes.peek().put("this", true);

        for(Stmt.Function func : stmt.methods){
            resolveFunction(func,  (func.name.getLexeme().equals("constructor") ? FunctionType.CONSTRUCTOR : FunctionType.METHOD));
        }

        endScope();


        if(hasBaseClass){
            endScope();
        }

        classType = outerType;
        return null;
    }
}
