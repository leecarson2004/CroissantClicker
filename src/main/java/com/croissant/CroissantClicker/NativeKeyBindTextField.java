package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.IntConsumer;


public class NativeKeyBindTextField extends JTextField implements NativeKeyListener, FocusListener, NativeMouseListener, MouseListener {

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
        addMouseListener(this);
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
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {
        GlobalScreen.removeNativeKeyListener(this);
        GlobalScreen.removeNativeMouseListener(this);

        setText(getKeyBindString());
        config.setInputCaptureMode(false);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (!config.isInputCaptureMode()) { return; }

        int inputKey = nativeEvent.getKeyCode();

        setKeyBind(inputKey);
        clearFocus();
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        if (!config.isInputCaptureMode()) { return; }

        int inputButton = nativeEvent.getButton();

        if (inputButton != NativeMouseEvent.NOBUTTON){
            setKeyBind(-inputButton);
            clearFocus();
        }
    }

    //activate capturing mode if user clicks field
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1 && !config.isInputCaptureMode()){
            config.setInputCaptureMode(true);

            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);

            setText("<" + getKeyBindString() + ">");
        }
    }

    //ignore normal swing keyboard input
    @Override
    protected void processKeyEvent(KeyEvent e) {}

    public void setOnKeyChanged(IntConsumer listener){
        keyChangedListener = listener;
    }

    private void clearFocus() {
        SwingUtilities.invokeLater(() -> {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearFocusOwner();
        });
    }

    //remove NativeKeyListener when component leaves ui hierarchy
    @Override
    public void removeNotify() {
        GlobalScreen.removeNativeKeyListener(this);
        GlobalScreen.removeNativeMouseListener(this);
        super.removeNotify();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
