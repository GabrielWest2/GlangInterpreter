package org.example;

import java.util.List;

public abstract class Expr {
 public interface Visitor<R> {
 R visitAssignExpr(Assign expr);
 R visitAdditionAssignExpr(AdditionAssign expr);
 R visitArrayAssignExpr(ArrayAssign expr);
 R visitAdditionArrayAssignExpr(AdditionArrayAssign expr);
 R visitBinaryExpr(Binary expr);
 R visitGetExpr(Get expr);
 R visitSetExpr(Set expr);
 R visitAdditionSetExpr(AdditionSet expr);
 R visitCallExpr(Call expr);
 R visitGroupingExpr(Grouping expr);
 R visitLiteralExpr(Literal expr);
 R visitArrayInitExpr(ArrayInit expr);
 R visitLogicalExpr(Logical expr);
 R visitUnaryExpr(Unary expr);
 R visitTernaryExpr(Ternary expr);
 R visitPostfixExpr(Postfix expr);
 R visitThisExpr(This expr);
 R visitVariableExpr(Variable expr);
 R visitSuperExpr(Super expr);
 }
 public static class Assign extends Expr {
 Assign(Token name, Expr value) {
 this.name = name;
 this.value = value;
 }

public final Token name;
public final Expr value;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitAssignExpr(this);
 }
 }
 public static class AdditionAssign extends Expr {
 AdditionAssign(Token name, Expr value) {
 this.name = name;
 this.value = value;
 }

public final Token name;
public final Expr value;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitAdditionAssignExpr(this);
 }
 }
 public static class ArrayAssign extends Expr {
 ArrayAssign(Expr postfix, Token eq, Expr value) {
 this.postfix = postfix;
 this.eq = eq;
 this.value = value;
 }

public final Expr postfix;
public final Token eq;
public final Expr value;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitArrayAssignExpr(this);
 }
 }
 public static class AdditionArrayAssign extends Expr {
 AdditionArrayAssign(Expr postfix, Token eq, Expr value) {
 this.postfix = postfix;
 this.eq = eq;
 this.value = value;
 }

public final Expr postfix;
public final Token eq;
public final Expr value;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitAdditionArrayAssignExpr(this);
 }
 }
 public static class Binary extends Expr {
 Binary(Expr left, Token operator, Expr right) {
 this.left = left;
 this.operator = operator;
 this.right = right;
 }

public final Expr left;
public final Token operator;
public final Expr right;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitBinaryExpr(this);
 }
 }
 public static class Get extends Expr {
 Get(Expr object, Token name) {
 this.object = object;
 this.name = name;
 }

public final Expr object;
public final Token name;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitGetExpr(this);
 }
 }
 public static class Set extends Expr {
 Set(Expr object, Token name, Expr value) {
 this.object = object;
 this.name = name;
 this.value = value;
 }

public final Expr object;
public final Token name;
public final Expr value;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitSetExpr(this);
 }
 }
 public static class AdditionSet extends Expr {
 AdditionSet(Expr object, Token name, Expr value) {
 this.object = object;
 this.name = name;
 this.value = value;
 }

public final Expr object;
public final Token name;
public final Expr value;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitAdditionSetExpr(this);
 }
 }
 public static class Call extends Expr {
 Call(Expr callee, Token paren, List<Expr> arguments) {
 this.callee = callee;
 this.paren = paren;
 this.arguments = arguments;
 }

public final Expr callee;
public final Token paren;
public final List<Expr> arguments;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitCallExpr(this);
 }
 }
 public static class Grouping extends Expr {
 Grouping(Expr expression) {
 this.expression = expression;
 }

public final Expr expression;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitGroupingExpr(this);
 }
 }
 public static class Literal extends Expr {
 Literal(Object value) {
 this.value = value;
 }

public final Object value;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitLiteralExpr(this);
 }
 }
 public static class ArrayInit extends Expr {
 ArrayInit(List<Expr> exprs) {
 this.exprs = exprs;
 }

public final List<Expr> exprs;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitArrayInitExpr(this);
 }
 }
 public static class Logical extends Expr {
 Logical(Expr left, Token operator, Expr right) {
 this.left = left;
 this.operator = operator;
 this.right = right;
 }

public final Expr left;
public final Token operator;
public final Expr right;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitLogicalExpr(this);
 }
 }
 public static class Unary extends Expr {
 Unary(Token operator, Expr right) {
 this.operator = operator;
 this.right = right;
 }

public final Token operator;
public final Expr right;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitUnaryExpr(this);
 }
 }
 public static class Ternary extends Expr {
 Ternary(Token operator, Expr cond, Expr exp1, Expr exp2) {
 this.operator = operator;
 this.cond = cond;
 this.exp1 = exp1;
 this.exp2 = exp2;
 }

public final Token operator;
public final Expr cond;
public final Expr exp1;
public final Expr exp2;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitTernaryExpr(this);
 }
 }
 public static class Postfix extends Expr {
 Postfix(Token operator, Expr left, Expr val) {
 this.operator = operator;
 this.left = left;
 this.val = val;
 }

public final Token operator;
public final Expr left;
public final Expr val;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitPostfixExpr(this);
 }
 }
 public static class This extends Expr {
 This(Token keyword) {
 this.keyword = keyword;
 }

public final Token keyword;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitThisExpr(this);
 }
 }
 public static class Variable extends Expr {
 Variable(Token name) {
 this.name = name;
 }

public final Token name;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitVariableExpr(this);
 }
 }
 public static class Super extends Expr {
 Super(Token keyword, Token method) {
 this.keyword = keyword;
 this.method = method;
 }

public final Token keyword;
public final Token method;

 @Override
 public <R> R accept(Visitor<R> visitor) {
 return visitor.visitSuperExpr(this);
 }
 }

 public abstract <R> R accept(Visitor<R> visitor);
}
