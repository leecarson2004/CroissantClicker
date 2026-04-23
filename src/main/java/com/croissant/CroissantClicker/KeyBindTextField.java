package com.croissant.CroissantClicker;

import javax.swing.*;
import java.awt.event.*;
import java.util.function.IntConsumer;

public class KeyBindTextField extends JTextField implements FocusListener, MouseListener {

    private int keyBind;
    private BindType type;
    private IntConsumer keyChangedListener;
    private final ClickerConfig config;

    public KeyBindTextField(int keyBind, BindType type, ClickerConfig config){
        super();
        this.config = config;

        setEditable(false);
        setHorizontalAlignment(JTextField.CENTER);
        setKeyBind(keyBind);
        setType(type);

        addFocusListener(this);
        addMouseListener(this);
    }

    private String getKeyBindString(){
        if (type == BindType.MOUSE) {

            return switch (keyBind) {
                case -1 -> "None";
                case MouseEvent.BUTTON1_DOWN_MASK -> "Mouse Left";
                case MouseEvent.BUTTON2_DOWN_MASK -> "Mouse Middle";
                case MouseEvent.BUTTON3_DOWN_MASK -> "Mouse Right";
                default -> "Mouse Button " + keyBind;
            };
        }
        else {
            return (this.keyBind == -1 ? "None" : KeyEvent.getKeyText(keyBind));
        }
    }

    public int getKeyBind(){
        return keyBind;
    }

    public BindType getType(){
        return type;
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

    public void setType(BindType type){
        this.type = type;
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
        if (!hasFocus()) return;

        int button = e.getButton();

        setKeyBind(button);
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