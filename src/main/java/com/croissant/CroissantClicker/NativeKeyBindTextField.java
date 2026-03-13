package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.IntConsumer;


public class NativeKeyBindTextField extends JTextField implements NativeKeyListener, FocusListener {

    private int keyBind;
    private IntConsumer keyChangedListener;
    private ClickerConfig config;

    public NativeKeyBindTextField(int keyBind, ClickerConfig config){
        super();
        this.config = config;

        setEditable(false);
        setHorizontalAlignment(JTextField.CENTER);
        setKeyBind(keyBind);

        addFocusListener(this);

        System.err.println("created new nativekeybindtextfield...");
    }

    private String getKeyBindString(){
        return (this.keyBind == -1 ? "None" : NativeKeyEvent.getKeyText(keyBind));
    }

    public int getKeyBind(){
        return keyBind;
    }

    public void setKeyBind(int keyBind){

        if (keyBind == NativeKeyEvent.VC_DELETE || keyBind == NativeKeyEvent.VC_BACKSPACE) {
            this.keyBind = -1;
        }
        else if (keyBind == NativeKeyEvent.VC_ENTER){
            return;
        }
        else{
            this.keyBind = keyBind;
        }

        setText(getKeyBindString());

        if (keyChangedListener != null){
            keyChangedListener.accept(this.keyBind);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        config.setCapturingNativeKeyBind(true);

        GlobalScreen.addNativeKeyListener(this);
        setText("<" + getKeyBindString() + ">");
    }

    @Override
    public void focusLost(FocusEvent e) {
        GlobalScreen.removeNativeKeyListener(this);
        setText(getKeyBindString());

        config.setCapturingNativeKeyBind(false);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (!hasFocus()){
            return;
        }

        int inputKey = nativeEvent.getKeyCode();

        SwingUtilities.invokeLater(() -> {
            setKeyBind(inputKey);
            transferFocus(); //exit field
        });
    }

    public void setOnKeyChanged(IntConsumer listener){
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
