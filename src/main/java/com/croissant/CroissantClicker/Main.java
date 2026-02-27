package com.croissant.CroissantClicker;

import java.awt.*;
import com.formdev.flatlaf.FlatDarkLaf;

//TODO: theme switching, other button functionality


public class Main {

    public static void main(String[] args) throws AWTException {

        FlatDarkLaf.setup();

        ClickerConfig config = new ClickerConfig();
        ClickerLogic logic = new ClickerLogic(config);
        GlobalHotkey hotkey = new GlobalHotkey(config);

        //load user save data into config (prior to propertychangelistener initialization)
        SaveDataManager.load(config, "current");

        //autosave user data on config change
        config.addPropertyChangeListener(evt -> {
            if ("clickLimit".equals(evt.getPropertyName())
                    || "cps".equals(evt.getPropertyName())
                    || "mouseButton".equals(evt.getPropertyName())
                    || "clickLimitMode".equals(evt.getPropertyName())){
                SaveDataManager.save(config, "current");
            }
        });

        javax.swing.SwingUtilities.invokeLater(() -> {
            ClickerUI ui = new ClickerUI(config, logic);
            ui.setVisible(true);
        });
    }
}