package com.gabe;

import java.util.*;

public class Parser {


    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        while(!isAtEnd()){
            statements.add(declaration());
        }

        return statements;
    }

    // Statement Rules

    private Stmt declaration(){
        if(match(TokenType.VAR)) return varDeclaration();

        return statement();
    }

    private Stmt varDeclaration(){
        Token var = consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expr init = null;
        if(match(TokenType.EQUAL)){
            init = expression();
        }
        consume(TokenType.SEMICOLON, "Line should be followed by semicolon");
        return new Stmt.Var(var, init);
    }


    private Stmt statement(){

        if(match(TokenType.CLASS)){
            return classStatement();
        }
        if(match(TokenType.PRINT)){
            return printStatement();
        }
        if(match(TokenType.LEFT_BRACE)){
            return new Stmt.Block(block());
        }
        if(match(TokenType.FOR)) {
            return forStatement();
        }
        if(match(TokenType.FOREACH)) {
            return forEachStatement();
        }
        if(match(TokenType.IF)){
            return ifStatement();
        }
        if(match(TokenType.WHILE)){
            return whileStatement();
        }
        if(match(TokenType.DEF)){
            return function("function");
        }
        if(match(TokenType.RETURN)){
            return returnStatement();
        }
        if(match(TokenType.BREAK)){
            return breakStatement();
        }
        if(match(TokenType.SWITCH)){
            return switchStatement();
        }
        return expressionStatement();

    }

    private Stmt switchStatement() {
        Token swt = prev();
        Token lp = consume(TokenType.LEFT_PAREN, "Expect ( after name switch");
        Expr expr = expression();
        Token rp = consume(TokenType.RIGHT_PAREN, "Expect ) after name switch expr");
        consume(TokenType.LEFT_BRACE, "Expect { before body of switch");

        Stmt defaultStmt = null;
        List<Expr> cases = new ArrayList<>();
        List<Stmt> caseBodies = new ArrayList<>();
        while (!match(TokenType.RIGHT_BRACE)){
            consume(TokenType.CASE, "Expect case declaration");
            if(match(TokenType.DEFAULT)){
                defaultStmt = statement();
            }else{
                Expr lit = primary();
                Stmt stmt = statement();
                cases.add(lit);
                caseBodies.add(stmt);
            }
        }

        return new Stmt.Switch(expr, swt, caseBodies, cases, defaultStmt);

    }

    private Stmt breakStatement() {
        Token brk = prev();
        consume(TokenType.SEMICOLON, "Line should be followed by semicolon");
        return new Stmt.Break(brk);
    }

    private Stmt classStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Expect class name");

        Expr.Variable base = null;
        if(match(TokenType.EXTENDS)){
            base = new Expr.Variable(consume(TokenType.IDENTIFIER, "Expect base class name"));
        }

        consume(TokenType.LEFT_BRACE, "Expect { before body of class");

        //force def?
        match(TokenType.DEF);


        List<Stmt.Function> methods = new ArrayList<>();
        while (!check(TokenType.RIGHT_BRACE)){
            methods.add(function("method"));
        }

        consume(TokenType.RIGHT_BRACE, "Expect } at end of class");

        return new Stmt.Class(name, methods, base);
    }

    private Stmt returnStatement() {
        Token rtrn = prev();
        // Optional return value
        Expr val = check(TokenType.SEMICOLON) ? null : expression();
        consume(TokenType.SEMICOLON, "Line should be followed by semicolon");

        return new Stmt.Return(rtrn, val);
    }

    /**
     * Parse function declaration
     * @param type {@code String} containing either 'function' or 'method'
     * @return the parsed function stmt
     */
    private Stmt.Function function(String type){
        Token name = consume(TokenType.IDENTIFIER, "Expected name of " + type);
        Token lp = consume(TokenType.LEFT_PAREN, "Expect ( after name of " + type);
        List<Token> params = new ArrayList<>();
        while(!check(TokenType.RIGHT_PAREN)){
            do {
                if(params.size() > 255){
                    App.parseError(lp, "Method cannot have more than 255 arguments.");
                    return null;
                }

                params.add(consume(TokenType.IDENTIFIER, "expected parameter name"));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ) after params of " + type);

        consume(TokenType.LEFT_BRACE, "Expect { before body of " + type);

        List<Stmt> body = block();
        return new Stmt.Function(name, params, body);
    }

    private Stmt forStatement() {
        consume(TokenType.LEFT_PAREN, "Expect ( after for");
        Stmt initializer = null;
        if(match(TokenType.SEMICOLON)){
            initializer = null;
        }else if(match(TokenType.VAR)){
            initializer = varDeclaration();
        }else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ; after loop condition");

        Expr increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }
        consume(TokenType.RIGHT_PAREN, "Expect ) after for clauses");
        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(
                    Arrays.asList(
                            body,
                            new Stmt.Expression(increment)));
        }

        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Stmt forEachStatement() {
        consume(TokenType.LEFT_PAREN, "Expect ( after foreach");
        consume(TokenType.VAR, "Expect 'var' token");
        Token var = consume(TokenType.IDENTIFIER, "Expect variable name.");
        //Stmt var = new Stmt.Var(name, null);
        Token colon = consume(TokenType.COLON, "Expect : after variable name");


        Expr iterable = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ) after foreach");
        Stmt body = statement();
        return new Stmt.ForEach(var, iterable, body, colon);
    }

    private Stmt whileStatement(){
        consume(TokenType.LEFT_PAREN, "Expect ( before if condition");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ) after if condition");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect ( before if condition");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ) after if condition");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private List<Stmt> block(){
        List<Stmt> stmts = new ArrayList<>();

        while(!isAtEnd() && !check(TokenType.RIGHT_BRACE)){
            stmts.add(declaration());
        }
        consume(TokenType.RIGHT_BRACE, "Expect closing brace.");

        return stmts;
    }

    private Stmt expressionStatement(){
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Line should be followed by semicolon");
        return new Stmt.Expression(expr);
    }

    private Stmt printStatement(){
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Line should be followed by semicolon");
        return new Stmt.Print(value);
    }




    // Expression Rules
    private Expr expression(){
        return assignment();
    }

    private Expr assignment() {
        Expr expr = assignment_add();
        if (match(TokenType.EQUAL)) {
            Token equals = prev();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            } else if(expr instanceof Expr.Get){
                Expr.Get get = ((Expr.Get)expr);
                return new Expr.Set(get.object, get.name, value);
            } else if(expr instanceof Expr.Postfix){
                Expr.Postfix postfix = ((Expr.Postfix)expr);
                if(postfix.operator.tokenType() == TokenType.LEFT_BRACKET){
                    return new Expr.ArrayAssign(postfix, equals, value);
                }
            }
            App.parseError(equals, "Invalid assignment target");
        }

        return expr;
     }

    private Expr assignment_add() {
        Expr expr = ternary();
        if (match(TokenType.PLUS_EQUALS, TokenType.MINUS_EQUALS)) {
            Token equals = prev();
            Expr value = assignment_add();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.AdditionAssign(name, value);
            } else if(expr instanceof Expr.Get){
                Expr.Get get = ((Expr.Get)expr);
                return new Expr.AdditionSet(get.object, get.name, value);
            }else if(expr instanceof Expr.Postfix){
                Expr.Postfix postfix = ((Expr.Postfix)expr);
                if(postfix.operator.tokenType() == TokenType.LEFT_BRACKET){
                    return new Expr.AdditionArrayAssign(postfix, equals, value);
                }
            }
            App.parseError(equals, "Invalid assignment target");
        }

        return expr;
    }

    private Expr ternary(){
        Expr cond = or();
        if (match(TokenType.QUESTION)) {
            Token symbol = prev();
            Expr exp1 = expression();
            Token seperator = consume(TokenType.COLON, "Expect colon separating expressions");
            Expr exp2 = expression();
            return new Expr.Ternary(symbol, cond, exp1, exp2);
        }
        return cond;
    }

    private Expr or(){
         Expr expr = xor();
         while (match(TokenType.OR)) {
             Token operator = prev();
             Expr right = xor();
             expr = new Expr.Logical(expr, operator, right);
         }
         return expr;
     }

    private Expr xor(){
        Expr expr = and();
        while (match(TokenType.XOR)) {
            Token operator = prev();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and(){
        Expr expr = equality();
        while (match(TokenType.AND)) {
            Token operator = prev();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr equality(){
        Expr expr = comparison();
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = prev();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison(){
        Expr expr = term();
        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)){
            Token op = prev();
            Expr right = term();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr term(){
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = prev();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor(){
        Expr expr = unary();
        while (match(TokenType.SLASH, TokenType.STAR, TokenType.PERCENT)) {
            Token operator = prev();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary(){
        if(match(TokenType.BANG, TokenType.MINUS, TokenType.PLUS_PLUS, TokenType.MINUS_MINUS)){
            Token operator = prev();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return call();
    }

    private Expr call(){
        Expr expr = postfix();

        while (true){
            if(match(TokenType.LEFT_PAREN)){
                expr = finishCall(expr);
            }else if(match(TokenType.DOT)) {
                Token name = consume(TokenType.IDENTIFIER, "Expected property");
                expr = new Expr.Get(expr, name);
            }else{
                break;
            }
        }
        return expr;
    }

    private Expr postfix(){
        Expr expr = primary();
        if(match(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS)){
            Token operator = prev();
            return new Expr.Postfix(operator, expr, null);
        }else if(match(TokenType.LEFT_BRACKET)){
            Expr postfix = null;
            do {
                Token operator = prev();
                Expr value = expression();
                consume(TokenType.RIGHT_BRACKET, "Expect closing ] here");
                if(postfix == null){
                    postfix = new Expr.Postfix(operator, expr, value);
                }else{
                    postfix = new Expr.Postfix(operator, postfix, value);
                }

            } while(match(TokenType.LEFT_BRACKET));
            return postfix;
        }
        return expr;
    }

    private Expr primary(){
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NULL)) return new Expr.Literal(null);
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(prev().literal());
        }
        if(match(TokenType.THIS)) return new Expr.This(prev());
        if(match(TokenType.SUPER)){
            Token keyword = prev();
            consume(TokenType.DOT, "Expected dot after super");
            Token method = consume(TokenType.IDENTIFIER, "Expected property of superclass");
            return new Expr.Super(keyword, method);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(prev());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expected closing parentheses.");
            return new Expr.Grouping(expr);
        }

        if(match(TokenType.LEFT_BRACKET)){
            ArrayList<Expr> exprs = new ArrayList<>();
            while(!match(TokenType.RIGHT_BRACKET)){
                exprs.add(expression());
                match(TokenType.COMMA);
            }
            return new Expr.ArrayInit(exprs);
        }

        App.parseError(curr(), "Unexpected token.");
        return null;
    }



    private Expr finishCall(Expr expr) {
        List<Expr> arguments = new ArrayList<>();

        if(!check(TokenType.RIGHT_PAREN)){
            do {
                arguments.add(expression());
                if(arguments.size() > 255){
                    App.parseError(prev(), "Method cannot have more than 255 arguments.");
                }
            } while (match(TokenType.COMMA));
        }

        Token paren = consume(TokenType.RIGHT_PAREN, "Expect ) after arguments");

        return new Expr.Call(expr, paren, arguments);
    }



    //////////////// Token matching utils
    public boolean match(TokenType... types){
        for(TokenType t : types){
            if(check(t)){
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String err){
        if(match(type)) return prev();

        App.parseError(curr(), err);
        return null;
    }

    private void advance() {
        if (!isAtEnd()) current++;
    }

    private boolean check(TokenType t) {
        if(isAtEnd()) return false;
        return tokens.get(current).tokenType() == t;
    }

    private Token curr(){
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return peek().tokenType() == TokenType.EOF;
    }

    private Token peek(){
        return tokens.get(current);
    }
    private Token prev(){
        return tokens.get(current - 1);
    }



}
