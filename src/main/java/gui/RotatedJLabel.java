package gui;

import java.awt.*;
import javax.swing.*;

/** A JLabel that can be rotated when it's created. */
public class RotatedJLabel extends JLabel {
    private double theta;

    /**
     * @param theta the angle of rotation in radians.
     */
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
