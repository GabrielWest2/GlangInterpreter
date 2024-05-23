package org.example.tools;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import org.example.Expr;
import org.example.Stmt;
import org.example.Token;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.*;

public class ASTGrapher extends JFrame implements Expr.Visitor<Node>, Stmt.Visitor<Node> {
    private static int id = 0;
    private static Graph g;
    private static Node node(String label){
        return Factory.node("node" + (id++) ).with(Label.of(label));
    }


    @Override
    public Node visitBlockStmt(Stmt.Block stmt) {
        Node n = node("{}");
        g = g.with(n);
        int i = 0;
        for(Stmt st : stmt.statements){
            Node arg = graphnode(st);
            g = g.with(n.link(to(arg).with(Label.of(Integer.toString(i)))));
            i++;
        }
        return n;
    }

    @Override
    public Node visitExpressionStmt(Stmt.Expression stmt) {
        return null;
    }

    @Override
    public Node visitFunctionStmt(Stmt.Function stmt) {
        Node n = node(stmt.name.getLexeme() + "()");
        g = g.with(n);
        int i = 0;
        for(Token param : stmt.params){
            Node arg = node(param.getLexeme());
            g = g.with(n.link(to(arg).with(Label.of("a"+i))));
            i++;
        }
        Node body = node("{}");
        g = g.with(n.link(body));
        i = 0;
        for(Stmt st : stmt.body){
            Node arg = graphnode(st);
            g = g.with(body.link(to(arg).with(Label.of(Integer.toString(i)))));
            i++;
        }
        return n;
    }

    @Override
    public Node visitIfStmt(Stmt.If stmt) {
        return null;
    }

    @Override
    public Node visitWhileStmt(Stmt.While stmt) {
        Node n = node("while");
        Node cond = graphnode(stmt.condition);
        Node body = graphnode(stmt.body);

        g = g.with(n.link(to(cond).with(Label.of("cond"))).link(body));

        return n;
    }

    @Override
    public Node visitPrintStmt(Stmt.Print stmt) {
        Node n = node("print");
        g = g.with(n.with(Color.CORNFLOWERBLUE));
        if(stmt.expression != null){
            Node val = graphnode(stmt.expression);
            g = g.with(n.link(val));
        }
        return n;
    }

    @Override
    public Node visitVarStmt(Stmt.Var stmt) {
        return null;
    }

    @Override
    public Node visitReturnStmt(Stmt.Return stmt) {
        Node n = node(stmt.keyword.getLexeme());
        g = g.with(n.with(Color.INDIANRED4));
        if(stmt.value != null){
            Node val = graphnode(stmt.value);
            g = g.with(n.link(val));
        }
        return n;
    }

    @Override
    public Node visitClassStmt(Stmt.Class stmt) {
        return null;
    }







    public class GraphComponent extends JComponent {

        private BufferedImage image;

        public void setImage(final BufferedImage image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null)
                g.drawImage((Image) image, 0, 0, (int) (image.getWidth() / 4.0), (int) (image.getHeight() / 4.0), this);
        }
    }

    public ASTGrapher makeGraph(Stmt stmt){
        graphnode(stmt);
        return this;
    }

    public ASTGrapher makeGraph(Expr expr){
        graphnode(expr);
        return this;
    }

    private Node graphnode(Expr expr){
        return expr.accept(this);
    }

    private Node graphnode(Stmt stmt){
        return stmt.accept(this);
    }

    public ASTGrapher(){
        g = graph("example1").directed()
                .graphAttr().with(Rank.dir(TOP_TO_BOTTOM))
                .nodeAttr().with(Font.name("Arial"))
                .linkAttr().with("class", "link-class").named("Statement");

    }

    public void showGraph(){
        GraphComponent component = new GraphComponent();
        BufferedImage img = Graphviz.fromGraph(g).height(3200).render(Format.PNG).toImage();
        component.setImage(img);
        getContentPane().add(component);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(img.getWidth()/4 + 20, img.getHeight()/4 + 40);
        setLocationRelativeTo(null); // Center the JFrame
        setVisible(true);
        setTitle("AST");
    }

    public static void main(String[] args) throws IOException {
        new ASTGrapher();
        //Graphviz.fromGraph(g).height(600).render(Format.PNG).toFile(new File("example/ex1.png"));
    }







    @Override
    public Node visitAssignExpr(Expr.Assign expr) {
        Node n = node("=");
        Node var = node(expr.name.getLexeme());
        Node value = graphnode(expr.value);
        g = g.with(n.link(to(value)).link(to(var)));
        return n;
    }

    @Override
    public Node visitAdditionAssignExpr(Expr.AdditionAssign expr) {
        Node n = node("+=");
        Node var = node(expr.name.getLexeme());
        Node value = graphnode(expr.value);
        g = g.with(n.link(to(var).with(Label.of("var"))).link(to(value).with(Label.of("val"))));
        return n;
    }

    @Override
    public Node visitBinaryExpr(Expr.Binary expr) {
        Node n = node(expr.operator.getLexeme());
        Node left = graphnode(expr.left);
        Node right = graphnode(expr.right);
        if(left == null) left = node("null");
        if(right == null) right = node("null");
        g = g.with(n.link(to(left)).link(to(right)));
        return n;
    }

    @Override
    public Node visitGetExpr(Expr.Get expr) {
        return null;
    }

    @Override
    public Node visitSetExpr(Expr.Set expr) {
        return null;
    }

    @Override
    public Node visitAdditionSetExpr(Expr.AdditionSet expr) {
        Node n = node(expr.name.getLexeme());
        Node val = graphnode(expr.value);
        g = g.with(n.link(to(val)));
        return n;
    }

    @Override
    public Node visitCallExpr(Expr.Call expr) {
        Node n = node("func()");
        int i = 0;
        for(Expr ex : expr.arguments){
            Node arg = graphnode(ex);
            g = g.with(n.link(to(arg).with(Label.of("a"+i))));
            i++;
        }
        return n;
    }

    @Override
    public Node visitGroupingExpr(Expr.Grouping expr) {
        Node n = node("()");
        Node child = graphnode(expr.expression);
        g = g.with(n.link(to(child)));
        return n;
    }

    @Override
    public Node visitLiteralExpr(Expr.Literal expr) {
        return node(expr.value.toString());
    }

    @Override
    public Node visitLogicalExpr(Expr.Logical expr) {
        Node n = node(expr.operator.getLexeme());
        Node left = graphnode(expr.left);
        Node right = graphnode(expr.right);
        g = g.with(n.link(to(left)).link(to(right)));
        return n;
    }

    @Override
    public Node visitUnaryExpr(Expr.Unary expr) {
        Node n = node(expr.operator.getLexeme());
        Node ex = graphnode(expr.right);
        g = g.with(n.link(to(ex)));
        return n;
    }

    @Override
    public Node visitThisExpr(Expr.This expr) {
        Node n = node(expr.keyword.getLexeme());
        return n;
    }

    @Override
    public Node visitVariableExpr(Expr.Variable expr) {
        Node n = node(expr.name.getLexeme());
        return n;
    }

    @Override
    public Node visitSuperExpr(Expr.Super expr) {
        Node n = node(expr.keyword.getLexeme() +"." + expr.method.getLexeme());
        return n;
    }
}
