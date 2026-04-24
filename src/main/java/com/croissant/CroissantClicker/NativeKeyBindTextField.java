package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.IntConsumer;


public class NativeKeyBindTextField extends JTextField implements NativeKeyListener, FocusListener, NativeMouseListener {

    private int keyBind;
    private IntConsumer keyChangedListener;
    private final ClickerConfig config;

    public NativeKeyBindTextField(int keyBind, ClickerConfig config){
        super();
        this.config = config;

        setCaretColor(new Color(0,0,0,0));
        setSelectedTextColor(getForeground());
        getCaret().setVisible(false);
        setCursor(Cursor.getDefaultCursor());
        setHorizontalAlignment(JTextField.CENTER);

        setKeyBind(keyBind);

        addFocusListener(this);
    }

    private String getKeyBindString(){
        if (keyBind == ClickerConfig.NO_KEY_BIND_SET){
            return "None";
        }

        if (keyBind < 0) {
            return "Mouse " + (-keyBind);
        }
        else{
            return NativeKeyEvent.getKeyText(keyBind);
        }
    }

    public int getKeyBind(){
        return keyBind;
    }

    public void setKeyBind(int keyBind){
        if (keyBind == NativeKeyEvent.VC_DELETE || keyBind == NativeKeyEvent.VC_BACKSPACE) {
            this.keyBind = ClickerConfig.NO_KEY_BIND_SET;
        }
        else if (keyBind == NativeKeyEvent.VC_ENTER || keyBind == -1){
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

        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);

        setText("<" + getKeyBindString() + ">");
    }

    @Override
    public void focusLost(FocusEvent e) {
        GlobalScreen.removeNativeKeyListener(this);
        GlobalScreen.removeNativeMouseListener(this);

        setText(getKeyBindString());
        config.setInputCaptureMode(false);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (!hasFocus()){
            return;
        }

        int inputKey = nativeEvent.getKeyCode();

        SwingUtilities.invokeLater(() -> {
            setKeyBind(inputKey);
            transferFocus();
        });
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        if (!hasFocus()){
            return;
        }

        int inputButton = nativeEvent.getButton();
        if (inputButton == NativeMouseEvent.NOBUTTON) return;


        SwingUtilities.invokeLater(() -> {
            setKeyBind(-inputButton);
            SwingUtilities.invokeLater(this::transferFocus);
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
