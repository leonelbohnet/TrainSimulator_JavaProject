package de.drvbund.lernlabit.lb.javaproject.model;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;


public class RotatebleIconLabel extends JLabel {
    private double angle = 0.0;
    private FlatSVGIcon icon;

    public RotatebleIconLabel(FlatSVGIcon icon) {
        super(icon);
        this.icon = icon;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        repaint();
    }

    public double getAngle() {
        return angle;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.rotate(Math.toRadians(angle), getWidth() / 2.0, getHeight() / 2.0);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        g2.rotate(Math.toRadians(angle), centerX, centerY);

        int iconX = (getWidth() - getIcon().getIconWidth()) / 2;
        int iconY = (getHeight() - getIcon().getIconHeight()) / 2;
        getIcon().paintIcon(this, g2, iconX, iconY);

        g2.dispose();
    }
}
