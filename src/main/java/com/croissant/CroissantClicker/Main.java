package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;


//ensure save data manager allows for storage of keybind class data?

public class Main {

    public static void main(String[] args) throws AWTException {

        setUpJNativeHook();

        ClickerConfig config = new ClickerConfig();
        ClickerLogic logic = new ClickerLogic(config);
        GlobalHotkey hotkey = new GlobalHotkey(config);

        //load user save data into config -- "_" denotes non-user created saved configuration file.
        SaveDataManager.load(config, "_current");

        //autosave user data on config change
        config.addPropertyChangeListener(evt -> {
            if ("clickLimit".equals(evt.getPropertyName())
                    || "cps".equals(evt.getPropertyName())
                    || "clickedButton".equals(evt.getPropertyName())
                    || "clickMode".equals(evt.getPropertyName())
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

    public static void setUpJNativeHook(){
        //register actual global nativehook
        try{
            GlobalScreen.registerNativeHook();
        } catch(NativeHookException e){
            System.err.println("Failed to register global native hook: " + e.getMessage());
            System.exit(1);
        }

        //Reduce JNativeHook logging noise
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        //Remove hook at shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ignored) {}
        }));
    }
}