package com.croissant.CroissantClicker;

import java.awt.*;

//Possible future features/refactors:
//TODO: make drawer page cleaner - methods making each drawer page?
//TODO: add enums for drop down boxes?
//TODO: add custom theme?
//TODO: user selection of image?

//For update 1.6:
//TODO: allow for user selection of hotkey?
//TODO: slide in drawer animation?
//TODO: allow for lower cps (delay between click mode)
// -> change cps spinner to delay spinner,
// -> use card panels to make the swap work
// -> work out swap logic in actual clickerlogic -- ensure efficiency
//add to input validation method

//TODO: ensure are you sure popup spawns where window is!



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
                    || "delay".equals(evt.getPropertyName())
                    || "delayMode".equals(evt.getPropertyName())
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