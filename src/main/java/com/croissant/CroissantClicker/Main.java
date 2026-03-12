package com.croissant.CroissantClicker;

import java.awt.*;

//Possible future features/refactors:
//TODO: make drawer page cleaner - methods making each drawer page?
//TODO: add enums for drop down boxes?
//TODO: add custom theme?
//TODO: user selection of image?

//For update 1.6:
//TODO: SIMPLE MODE VS ADVANCED -- simple has CPS, advanced has DELAY BETWEEN CLICKS (MS), LENGTH OF CLICK (hold down)
// AND this covers image so no image in advanced (estimated cps display)
// add default delay between click, adjustable by user, use different clickerlogic depending on mode
// (separate into 2 methods).
// UX: in mode -- repeat, HOLD, (maybe checkbox for until stopped (for both repeat and hold) gray out things, add hold time option

//***release mouse with finally to prevent issues with mouse staying held down if thread crashes
// area to enter click pattern?? (LLR, RRL, or even with characters?)


//time between click start and release slider basically?
//TODO: allow for user selection of hotkey? -- current
//TODO: slide in drawer animation?
//TODO: remove excessive numbers of buttons for using drawer -- closing drawer only on top with arrow or by clicking off
//done:
//spaces in user input allowed
//delay mode for millisecond delay specification (longer delays allows)
//ensure are you sure popup spawns where window is!
//fixed bug where inputs >1000 wouldn't work as click limit





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
                    || "hotkey".equals(evt.getPropertyName())
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