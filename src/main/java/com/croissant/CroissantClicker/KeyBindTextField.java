package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.IntConsumer;


public class KeyBindTextField extends JTextField implements NativeKeyListener, FocusListener {

    private int keyBind;
    private IntConsumer keyChangedListener;

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

        if (keyBind == NativeKeyEvent.VC_DELETE || keyBind == NativeKeyEvent.VC_BACKSPACE) {
            this.keyBind = -1;
        } else{
            this.keyBind = keyBind;
        }

        SwingUtilities.invokeLater(() -> setText(keyBind == -1 ? "None" : getKeyBindString()));

        if (keyChangedListener != null){
            keyChangedListener.accept(keyBind);
        }
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

        setKeyBind(inputKey);

        //exit field
        SwingUtilities.invokeLater(this::transferFocus);
    }

    public void addKeyChangedListener(IntConsumer listener){
        keyChangedListener = listener;
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
