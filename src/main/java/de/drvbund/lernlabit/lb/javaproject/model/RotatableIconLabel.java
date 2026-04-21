package de.drvbund.lernlabit.lb.javaproject.model;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JLabel that can display a rotated SVG icon.
 * This component is useful for creating loading animations or directional indicators.
 * The icon rotates around its center point with anti-aliasing for smooth rendering.
 *
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class RotatableIconLabel extends JLabel {
    /** The current rotation angle in degrees (0-360) */
    private double angle = 0.0;
    
    /** The SVG icon to be displayed and rotated */
    private FlatSVGIcon icon;

    /**
     * Creates a new rotatable icon label with the specified SVG icon.
     * 
     * @param icon the FlatSVGIcon to display and rotate
     */
    public RotatableIconLabel(FlatSVGIcon icon) {
        super(icon);
        this.icon = icon;
    }

    /**
     * Sets the rotation angle for the icon.
     * Automatically triggers a repaint to show the new rotation.
     * 
     * @param angle the rotation angle in degrees (typically 0-360)
     */
    public void setAngle(double angle) {
        this.angle = angle;
        repaint();
    }

    /**
     * Gets the current rotation angle of the icon.
     * 
     * @return the rotation angle in degrees
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Custom paint method that renders the icon with rotation.
     * Uses Graphics2D transformations to rotate the icon around its center point.
     * Anti-aliasing is enabled for smooth edges during rotation.
     * 
     * @param g the Graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Create a copy of the graphics context to avoid affecting other components
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Enable anti-aliasing for smooth rotation
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate center point for rotation
        g2.rotate(Math.toRadians(angle), getWidth() / 2.0, getHeight() / 2.0);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        // Apply rotation around center (note: this appears to be a duplicate rotation call)
        g2.rotate(Math.toRadians(angle), centerX, centerY);

        // Center the icon within the label bounds
        int iconX = (getWidth() - getIcon().getIconWidth()) / 2;
        int iconY = (getHeight() - getIcon().getIconHeight()) / 2;
        
        // Paint the icon with the applied transformations
        getIcon().paintIcon(this, g2, iconX, iconY);

        // Dispose of the graphics context copy
        g2.dispose();
    }
}
