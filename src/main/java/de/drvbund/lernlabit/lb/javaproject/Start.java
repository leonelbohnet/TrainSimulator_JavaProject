package de.drvbund.lernlabit.lb.javaproject;

import com.formdev.flatlaf.FlatDarkLaf;
import de.drvbund.lernlabit.lb.javaproject.view.MainFrame;


public class Start {
    public static void main(String[] args) {
        FlatDarkLaf.setup();
//        FlatIntelliJLaf.setup();
//        FlatDarculaLaf.setup();

        new MainFrame().setVisible(true);
    }
}
