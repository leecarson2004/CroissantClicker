package com.croissant.CroissantClicker;

import java.awt.*;

//TODO: add enums for drop down boxes
//TODO: add custom theme
//TODO: slide in drawer animation
//TODO: allow for user selection of hotkey
//TODO: user selection of image?

//TODO: when user tries to delete save config, popup dialouge should show omniman - are you sure?

//TODO: ensure user input is sanitized against special characters (only letters allowed)

//TODO: gray out load button if no input ready

public class Main {

    public static void main(String[] args) throws AWTException {

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
                    || "clickLimitMode".equals(evt.getPropertyName())
                    || "theme".equals(evt.getPropertyName())
            ){
                SaveDataManager.save(config, "current");
            }
        });

        javax.swing.SwingUtilities.invokeLater(() -> {
            ClickerUI ui = new ClickerUI(config, logic);
            ThemeManager.setTheme(config.getTheme(), ui);

            ui.setVisible(true);
        });
    }
}