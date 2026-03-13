package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class GlobalHotkey implements NativeKeyListener {

    private final ClickerConfig config;

    public GlobalHotkey(ClickerConfig config){
        this.config = config;

        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e){
        if (config.isCapturingNativeKeyBind()){
            return;
        }

        //global hotkey:
        if (e.getKeyCode() == config.getHotkey()){

            config.setEnabled(!config.isEnabled());
        }
    }
}
