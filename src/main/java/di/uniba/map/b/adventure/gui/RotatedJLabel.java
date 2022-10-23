package di.uniba.map.b.adventure.gui;

import java.awt.*;
import javax.swing.*;

public class RotatedJLabel extends JLabel {
    private double theta;

    public RotatedJLabel(double theta) {
        super();
        this.theta = theta;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D gx = (Graphics2D) g;
        gx.rotate(theta, getX() + getWidth() / 2, getY() + getHeight() / 2);
        super.paintComponent(g);
    }
}
