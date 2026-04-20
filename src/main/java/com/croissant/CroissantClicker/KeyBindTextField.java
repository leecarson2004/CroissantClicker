package com.croissant.CroissantClicker;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.function.IntConsumer;

public class KeyBindTextField extends JTextField implements FocusListener {

    private int keyBind;
    private IntConsumer keyChangedListener;
    private final ClickerConfig config;

    public KeyBindTextField(int keyBind, ClickerConfig config){
        super();
        this.config = config;

        setEditable(false);
        setHorizontalAlignment(JTextField.CENTER);
        setKeyBind(keyBind);

        addFocusListener(this);
    }

    private String getKeyBindString(){
        return (this.keyBind == -1 ? "None" : KeyEvent.getKeyText(keyBind));
    }

    public int getKeyBind(){
        return keyBind;
    }

    public void setKeyBind(int keyBind){
        if (keyBind == KeyEvent.VK_DELETE || keyBind == KeyEvent.VK_BACK_SPACE){
            this.keyBind = -1;
        }
        else if (keyBind == KeyEvent.VK_ENTER){
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
        config.setInputCaptureMode(true);
        setText("<" + getKeyBindString() + ">");
    }

    @Override
    public void focusLost(FocusEvent e) {
        setText(getKeyBindString());
        config.setInputCaptureMode(false);
    }

    @Override
    protected void processKeyEvent(KeyEvent e){
        if (e.getID() == KeyEvent.KEY_PRESSED){
            int inputKey = e.getKeyCode();

            setKeyBind(inputKey);
            transferFocus(); //exit field
        }

        e.consume(); //stop processing of event
    }

    public void setOnKeyChanged(IntConsumer listener){
        keyChangedListener = listener;
    }
}