package com.croissant.CroissantClicker;

import javax.swing.*;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class ThemeManager {

    public static void setTheme(String themeName, JFrame frame){
        try{
            switch (themeName){
                case "Dark":
                    FlatDarkLaf.setup();
                    break;
                case "Light":
                    FlatLightLaf.setup();
                    break;
                default:
                    FlatDarkLaf.setup();
            }

            SwingUtilities.updateComponentTreeUI(frame);
            frame.repaint();

        } catch (Exception e){
            System.err.println("Error switching theme");
            e.printStackTrace();
        }
    }
}
