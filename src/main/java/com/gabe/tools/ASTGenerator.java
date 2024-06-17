package com.gabe.tools;

// FROM CRAFTING INTERPRETERS

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class ASTGenerator {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        defineAst("./src/main/java/org/example", "Expr", Arrays.asList(
                "Assign : Token name, Expr value",
                "AdditionAssign : Token name, Expr value",
                "ArrayAssign : Expr postfix, Token eq, Expr value",
                "AdditionArrayAssign : Expr postfix, Token eq, Expr value",
                "Binary : Expr left, Token operator, Expr right",
                "Get : Expr object, Token name",
                "Set : Expr object, Token name, Expr value",
                "AdditionSet : Expr object, Token name, Expr value",
                "Call : Expr callee, Token paren, List<Expr> arguments",
                "Grouping : Expr expression",
                "Literal : Object value",
                "ArrayInit : List<Expr> exprs",
                "Logical : Expr left, Token operator, Expr right",
                "Unary : Token operator, Expr right",
                "Ternary : Token operator, Expr cond, Expr exp1, Expr exp2",
                "Postfix : Token operator, Expr left, Expr val",
                "This : Token keyword",
                "Variable : Token name",
                "Super : Token keyword, Token method"
        ));
        defineAst("./src/main/java/org/example", "Stmt", Arrays.asList(
                "Block : List<Stmt> statements",
                "Expression : Expr expression",
                "Function : Token name, List<Token> params, List<Stmt> body",
                "If : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "While : Expr condition, Stmt body",
                "ForEach : Token var, Expr iterable, Stmt body, Token colon",
                "Print : Expr expression",
                "Var : Token name, Expr initializer",
                "Return : Token keyword, Expr value",
                "Switch : Expr expression, Token keyword, List<Stmt> caseBodies, List<Expr> caseValues, Stmt defaultCase",
                "Break : Token keyword",
                "Class : Token name, List<Stmt.Function> methods, Expr.Variable base"
        ));
    }

    private static void defineAst(
            String outputDir, String baseName, List<String> types)
            throws FileNotFoundException, UnsupportedEncodingException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println("package org.example;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public abstract class " + baseName + " {");
        defineVisitor(writer, baseName, types);
        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
        writer.println();
        writer.println(" public abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineType(
            PrintWriter writer, String baseName,
            String className, String fieldList) {
        writer.println(" public static class " + className + " extends " +
                baseName + " {");
        // Constructor.
        writer.println(" " + className + "(" + fieldList + ") {");
        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println(" this." + name + " = " + name + ";");
        }
        writer.println(" }");
        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println("public final " + field + ";");
        }
        // Visitor pattern.
        writer.println();
        writer.println(" @Override");
        writer.println(" public <R> R accept(Visitor<R> visitor) {");
        writer.println(" return visitor.visit" +
                className + baseName + "(this);");
        writer.println(" }");
        writer.println(" }");
    }

    private static void defineVisitor(
            PrintWriter writer, String baseName, List<String> types) {
        writer.println(" public interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(" R visit" + typeName + baseName + "(" +
                    typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println(" }");
    }
}
