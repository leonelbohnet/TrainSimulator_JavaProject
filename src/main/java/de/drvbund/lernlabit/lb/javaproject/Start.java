package de.drvbund.lernlabit.lb.javaproject;

import com.formdev.flatlaf.FlatDarkLaf;
import de.drvbund.lernlabit.lb.javaproject.view.MainFrame;

/**
 * Entry point for the Train Simulator application.
 * This class initializes the look and feel and launches the main application window.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class Start {
    
    /**
     * Main method that starts the application.
     * Sets up the FlatLaf dark theme and creates the main application frame.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize FlatLaf Dark Look and Feel for modern UI appearance
        FlatDarkLaf.setup();
        // Alternative themes available:
        // FlatIntelliJLaf.setup();
        // FlatDarculaLaf.setup();

        // Create and display the main application window
        new MainFrame().setVisible(true);
    }
}
