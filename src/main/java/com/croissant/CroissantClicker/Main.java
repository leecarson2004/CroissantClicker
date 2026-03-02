package com.croissant.CroissantClicker;

import java.awt.*;

//Possible future features/refactors:
//TODO: add enums for drop down boxes?
//TODO: add custom theme?
//TODO: slide in drawer animation?
//TODO: allow for user selection of hotkey?
//TODO: user selection of image?
//TODO: allow for lower cps (delay between click mode)
// -> change cps spinner to delay spinner,

public class Main {

    public static void main(String[] args) throws AWTException {

        ClickerConfig config = new ClickerConfig();
        ClickerLogic logic = new ClickerLogic(config);
        GlobalHotkey hotkey = new GlobalHotkey(config);

        //load user save data into config -- "_" denotes non-user created saved configuration file.
        SaveDataManager.load(config, "_current");

        //autosave user data on config change
        config.addPropertyChangeListener(evt -> {
            if ("clickLimit".equals(evt.getPropertyName())
                    || "cps".equals(evt.getPropertyName())
                    || "mouseButton".equals(evt.getPropertyName())
                    || "clickLimitMode".equals(evt.getPropertyName())
                    || "theme".equals(evt.getPropertyName())
            ){
                SaveDataManager.save(config, "_current");
            }
        });

        //ui initialization
        javax.swing.SwingUtilities.invokeLater(() -> {
            ClickerUI ui = new ClickerUI(config, logic);
            ThemeManager.setTheme(config.getTheme(), ui);

            ui.setVisible(true);
        });
    }
}