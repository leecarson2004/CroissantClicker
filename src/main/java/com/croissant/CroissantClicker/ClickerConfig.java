package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Objects;



public class ClickerConfig {

    //user selected options:
    private int clickedButton; //button actually clicked by clicker

    private boolean delayMode; //mode for click speed -- cps or delay
    private int cps; //clicks/sec
    private int delay; //delay between clicks
    private String clickMode; //click mode
    private int clickLimit; //num mouse clicks run when turned on
    private int hotkey; //hotkey
    private String theme;
    //----------------------------------------
    //clicker state:
    private boolean enabled = false; //whether clicker is enabled or not enabled
    private final AtomicInteger clickCount = new AtomicInteger(0); //num clicks ran in current run of autoclicker
    //data update states:
    private boolean updatingFromConfig = false; //flag indicating whether clicker ui is currently being updated with new config data
    private volatile boolean inputCaptureMode = false; //if a field is capturing input, JNativeHook ignores hotkey presses
    //----------------------------------------
    //constants
    public static final String APP_VERSION = "1.6.2";
    //User input bounds constants:
    public static final int DELAY_MIN = 20;
    public static final int DELAY_MAX = 10000;
    public static final int CPS_MIN = 1;
    public static final int CPS_MAX = 50;
    public static final int CLICK_LIMIT_MIN = 1;
    public static final int CLICK_LIMIT_MAX = 999_999;
    //default input values constants:
    public static final int MOUSE_BUTTON_DEFAULT = InputEvent.BUTTON1_DOWN_MASK;
    public static final boolean DELAY_MODE_DEFAULT = false;
    public static final int CPS_DEFAULT = 5;
    public static final int DELAY_DEFAULT = 200;
    public static final String CLICK_MODE_DEFAULT = "Unlimited Clicks";
    public static final int CLICK_LIMIT_DEFAULT = 50;
    public static final int HOTKEY_DEFAULT =  NativeKeyEvent.VC_F8;
    public static final String THEME_DEFAULT = "Dark";
    //main JFrame dims:
    public static final int WINDOW_WIDTH = 400;
    public static final int WINDOW_HEIGHT = 290;




    public ClickerConfig(){
        setDefaultConfig();
    }

    public void setDefaultConfig(){
        setClickedButton(MOUSE_BUTTON_DEFAULT);
        setDelayMode(DELAY_MODE_DEFAULT);
        setCps(CPS_DEFAULT);
        setDelay(DELAY_DEFAULT);
        setClickMode(CLICK_MODE_DEFAULT);
        setClickLimit(CLICK_LIMIT_DEFAULT);
        setTheme(THEME_DEFAULT);
        setHotkey(HOTKEY_DEFAULT);
    }

    //listener system initialization
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener configChangeListener){
        support.addPropertyChangeListener(configChangeListener);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        //null-safe comparison
        if (Objects.equals(this.theme, theme)) return;

        String old = this.theme;
        this.theme = theme;
        support.firePropertyChange("theme",old,theme);
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

    public boolean isDelayMode() {
        return delayMode;
    }
    public void setDelayMode(boolean delayMode) {
        if (this.delayMode == delayMode) return;

        boolean old = this.delayMode;
        this.delayMode = delayMode;
        support.firePropertyChange("delayMode",old,delayMode); //notify listeners
    }

    public int getDelay() {
        return delay;
    }
    public void setDelay(int delay) {
        if (this.delay == delay) return;

        int old = this.delay;
        this.delay = delay;
        support.firePropertyChange("delay",old,delay); //notify listeners
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
    public String getHotkeyString(){
        return hotkey == -1 ? "None" : NativeKeyEvent.getKeyText(hotkey);
    }
    public void setHotkey(int hotkey) {
        if (this.hotkey == hotkey) return;

        int old = this.hotkey;
        this.hotkey = hotkey;
        support.firePropertyChange("hotkey",old,hotkey);
    }

    public int getClickedButton() {
        return clickedButton;
    }
    public void setClickedButton(int clickedButton) {
        if (this.clickedButton == clickedButton) return;

        int old = this.clickedButton;
        this.clickedButton = clickedButton;
        support.firePropertyChange("mouseButton",old, clickedButton); //notify listeners
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

    public String getClickMode() {
        return clickMode;
    }
    public void setClickMode(String clickMode) {
        if (this.clickMode != null && this.clickMode.equals(clickMode)) return;

        String old = this.clickMode;
        this.clickMode = clickMode;
        support.firePropertyChange("clickMode",old, clickMode); //notify listeners
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

    public boolean isUpdatingFromConfig() {
        return updatingFromConfig;
    }
    public void setUpdatingFromConfig(boolean updatingFromConfig) {
        this.updatingFromConfig = updatingFromConfig;
    }

    public boolean isInputCaptureMode() {
        return inputCaptureMode;
    }
    public void setInputCaptureMode(boolean inputCaptureMode) {
        this.inputCaptureMode = inputCaptureMode;
    }
}
