package com.croissant.CroissantClicker;

import javax.swing.*;
import java.awt.event.*;
import java.util.function.IntConsumer;

public class KeyBindTextField extends JTextField implements FocusListener, MouseListener {

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
        addMouseListener(this);
    }

    private String getKeyBindString(){
        if (keyBind == ClickerConfig.NO_KEY_BIND_SET){
            return "None";
        }

        if (keyBind < 0) {
            return switch (keyBind) {
                case -1 -> "Mouse Left";
                case -2 -> "Mouse Middle";
                case -3 -> "Mouse Right";
                default -> "Mouse Button " + (-keyBind);
            };
        }
        else {
            return KeyEvent.getKeyText(keyBind);
        }
    }

    public int getKeyBind(){
        return keyBind;
    }

    public void setKeyBind(int keyBind){
        if (keyBind == KeyEvent.VK_DELETE || keyBind == KeyEvent.VK_BACK_SPACE){
            this.keyBind = ClickerConfig.NO_KEY_BIND_SET;
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

    @Override
    public void mousePressed(MouseEvent e) {
        if (!config.isInputCaptureMode()) return;

        int button = e.getButton();
        if (button == MouseEvent.NOBUTTON) return;

        setKeyBind(-button);
        transferFocus();
    }

    //unused
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
}