package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class KeyBindTextField extends JTextField implements NativeKeyListener, FocusListener {

    private int keyBind;

    public KeyBindTextField(int keyBind){
        super();
        this.keyBind = keyBind;

        setEditable(false);
        setHorizontalAlignment(JTextField.CENTER);
        setText(getKeyBindString());

        addFocusListener(this);
    }

    private String getKeyBindString(){
        if (keyBind == -1){
            return getText();
        } else{
            return NativeKeyEvent.getKeyText(keyBind);
        }
    }

    public int getKeyBind(){
        return keyBind;
    }

    public void setKeyBind(int keyBind){
        this.keyBind = keyBind;
        setText(getKeyBindString());
    }

    @Override
    public void focusGained(FocusEvent e) {
        GlobalScreen.removeNativeKeyListener(this); //remove any duplicates if exist
        GlobalScreen.addNativeKeyListener(this);

        setText("<" + getKeyBindString() + ">");
    }

    @Override
    public void focusLost(FocusEvent e) {
        GlobalScreen.removeNativeKeyListener(this);

        setText(getKeyBindString());
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (!hasFocus()){
            return;
        }

        int inputKey = nativeEvent.getKeyCode();

        if (inputKey == NativeKeyEvent.VC_DELETE || inputKey == NativeKeyEvent.VC_BACKSPACE) {
            keyBind = -1;
        } else{
            keyBind = inputKey;
        }

        SwingUtilities.invokeLater(() -> {
            setText(keyBind == -1 ? "None" : getKeyBindString());
            transferFocus(); //exit field
        });
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

    //remove NativeKeyListener when component leaves ui hierarchy
    @Override
    public void removeNotify() {
        GlobalScreen.removeNativeKeyListener(this);
        super.removeNotify();
    }
}
