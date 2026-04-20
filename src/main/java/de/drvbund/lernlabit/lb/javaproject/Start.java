package de.drvbund.lernlabit.lb.javaproject;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;


public class Start {
    public static void main(String[] args) {
        FlatDarkLaf.setup();
//        FlatIntelliJLaf.setup();
//        FlatDarculaLaf.setup();

        new MainFrame().setVisible(true);
    }
}
