package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicInteger;


public class ClickerConfig {

    private int hotkey = NativeKeyEvent.VC_F8; //hotkey
    //user selected options:
    private int mouseButton; //mouse button
    private volatile int cps; //clicks/sec
    private boolean clickLimitMode; //infinite or finite run mode
    private int clickLimit; //num mouse clicks run when turned on
    //----------------------------------------
    //clicker state:
    private volatile boolean enabled = false; //whether clicker is enabled or not enabled
    private final AtomicInteger clickCount = new AtomicInteger(0); //num clicks ran in current run of autoclicker
    //----------------------------------------
    public static final String APP_VERSION = "1.4";
    //User input bounds constants:
    public static final int CPS_MIN = 1;
    public static final int CPS_MAX = 50;
    public static final int CLICK_LIMIT_MIN = 1;
    public static final int CLICK_LIMIT_MAX = 999_999;
    //default input values constants:
    public static final int MOUSE_BUTTON_DEFAULT = InputEvent.BUTTON1_DOWN_MASK;
    public static final int CPS_DEFAULT = 5;
    public static final boolean CLICK_LIMIT_MODE_DEFAULT = false;
    public static final int CLICK_LIMIT_DEFAULT = 50;



    public ClickerConfig(){
        setDefaultConfig();
    }

    public void createConfigTemplate(String configName){
        SaveDataManager.save(this,configName);
    }
    public void loadConfigTemplate(String configName){
        SaveDataManager.load(this,configName);
    }
    public void deleteConfigTemplate(String configName){
        SaveDataManager.delete(configName);
    }

    public void setDefaultConfig(){
        setMouseButton(MOUSE_BUTTON_DEFAULT);
        setCps(CPS_DEFAULT);
        setClickLimitMode(CLICK_LIMIT_MODE_DEFAULT);
        setClickLimit(CLICK_LIMIT_DEFAULT);
    }

    //listener system initialization
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener configChangeListener){
        support.addPropertyChangeListener(configChangeListener);
    }

    public int getCps() {
        return cps;
    }
    public void setCps(int cps){
        if (this.cps == cps) return;

        int old = this.cps;
        this.cps = cps;
        support.firePropertyChange("cps",old,cps); //notify listeners
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        boolean old = this.enabled;
        this.enabled = enabled;
        support.firePropertyChange("enabled",old,enabled); //notify listeners
    }

    public int getHotkey() {
        return hotkey;
    }
    public void setHotkey(int hotkey) {
        this.hotkey = hotkey;
    }

    public int getMouseButton() {
        return mouseButton;
    }
    public void setMouseButton(int mouseButton) {
        if (this.mouseButton == mouseButton) return;

        int old = this.mouseButton;
        this.mouseButton = mouseButton;
        support.firePropertyChange("mouseButton",old,mouseButton); //notify listeners
    }

    public int getClickLimit() {
        return clickLimit;
    }
    public void setClickLimit(int clickLimit) {
        if (this.clickLimit == clickLimit) return;

        int old = this.clickLimit;
        this.clickLimit = clickLimit;
        support.firePropertyChange("clickLimit",old,clickLimit); //notify listeners
    }

    public boolean isClickLimitMode() {
        return clickLimitMode;
    }
    public void setClickLimitMode(boolean clickLimitMode) {
        if (this.clickLimitMode == clickLimitMode) return;

        boolean old = this.clickLimitMode;
        this.clickLimitMode = clickLimitMode;
        support.firePropertyChange("clickLimitMode",old,clickLimitMode); //notify listeners
    }

    public int getClickCount() {
        return clickCount.get();
    }
    public void setClickCount(int value) {
        clickCount.set(value);
    }
    public void incrementClickCount() {
        clickCount.getAndIncrement();
    }
}
