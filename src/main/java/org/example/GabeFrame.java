package org.example;


import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GabeFrame extends JFrame {
    public class GabePanel extends JPanel {

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
        gabePanel = new GabePanel();
        add(gabePanel);
    }

    public void setPaintMethod(GCallable paintMethod) {
        this.paintMethod = paintMethod;
    }
}
