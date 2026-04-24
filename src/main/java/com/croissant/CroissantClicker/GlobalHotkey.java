package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

public class GlobalHotkey implements NativeKeyListener, NativeMouseListener {

    private final ClickerConfig config;

    public GlobalHotkey(ClickerConfig config){
        this.config = config;

        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e){
        if (config.isInputCaptureMode()){
            return;
        }

        if (e.getKeyCode() == config.getHotkey()){

            config.setEnabled(!config.isEnabled());
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e){
        if (config.isInputCaptureMode()){
            return;
        }

        if (e.getButton() == (-config.getHotkey())){

            config.setEnabled(!config.isEnabled());
        }
    }
}
