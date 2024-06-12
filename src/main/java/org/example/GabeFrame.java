package org.example;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class GabeFrame extends JFrame implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("CLICKED YAY");
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public class GabePanel extends JPanel {
        public GabePanel(MouseListener mouseListener) {
            addMouseListener(mouseListener);
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(paintMethod != null){
                GClass graphicsClass = i.wrapperClasses.get("Graphics");
                Object graphicsInstance = graphicsClass.call(i, List.of(g));
                paintMethod.call(i, List.of(graphicsInstance));
            }
        }


    }


    private final Interpreter i;
    private GCallable paintMethod;
    private GabePanel gabePanel;

    public GabeFrame(String title, int width, int height, Interpreter i) {
        this.i = i;
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gabePanel = new GabePanel(this);
        add(gabePanel);
        addMouseListener(this);
    }

    public void setPaintMethod(GCallable paintMethod) {
        this.paintMethod = paintMethod;
    }
}
