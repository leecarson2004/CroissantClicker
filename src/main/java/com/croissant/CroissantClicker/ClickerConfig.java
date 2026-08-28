package com.croissant.CroissantClicker;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;


public class ClickerConfig {

    //user selected options:
    private int clickedButton; //button actually clicked by clicker -- negative indicates mouse buttons, positive indicates keyboard buttons
    private boolean delayMode; //mode for click speed -- cps or delay
    private boolean timerMode; //mode for click limiter -- timed or click limit
    private int cps; //clicks/sec
    private int delay; //delay between clicks
    private String clickMode; //click mode
    private int clickLimit; //num mouse clicks run when turned on
    private int timeLimit; //time limit before halting clicks when turned on
    private int hotkey; //hotkey
    private String theme; //app color theme
    //----------------------------------------
    //clicker state:
    private boolean enabled = false; //whether clicker is enabled or not enabled
    private final AtomicInteger clickCount = new AtomicInteger(0); //num clicks ran in current run of autoclicker
    private final AtomicLong elapsedTime = new AtomicLong(0); //elapsed time since clicker logic start
    //data update states:
    private boolean updatingFromConfig = false; //flag indicating whether clicker ui is currently being updated with new config data
    private volatile boolean inputCaptureMode = false; //if a field is capturing input, JNativeHook ignores hotkey presses
    //----------------------------------------
    //constants
    public static final String APP_VERSION = "1.6.6";
    //User input bounds constants:
    public static final int DELAY_MIN = 20;
    public static final int DELAY_MAX = 9_999_999;
    public static final int CPS_MIN = 1;
    public static final int CPS_MAX = 50;

    public static final int CLICK_LIMIT_MIN = 1;
    public static final int CLICK_LIMIT_MAX = 999_999;
    public static final int TIME_LIMIT_MIN = 1;
    public static final int TIME_LIMIT_MAX = 999_999;

    public static final int NO_KEY_BIND_SET = -999;
    //default input values constants:
    public static final int CLICKED_BUTTON_DEFAULT = -1;
    public static final boolean DELAY_MODE_DEFAULT = false;
    public static final boolean TIMER_MODE_DEFAULT = false;
    public static final int CPS_DEFAULT = 5;
    public static final int DELAY_DEFAULT = 200;
    public static final String CLICK_MODE_DEFAULT = "Unlimited Clicks";
    public static final int CLICK_LIMIT_DEFAULT = 50;
    public static final int TIME_LIMIT_DEFAULT = 30;
    public static final int HOTKEY_DEFAULT =  NativeKeyEvent.VC_F8;
    public static final String THEME_DEFAULT = "Dark";
    //main JFrame dims:
    public static final int WINDOW_WIDTH = 400;
    public static final int WINDOW_HEIGHT = 290;




    public ClickerConfig(){
        setDefaultConfig();
    }

    public void setDefaultConfig(){
        setClickedButton(CLICKED_BUTTON_DEFAULT);
        setDelayMode(DELAY_MODE_DEFAULT);
        setTimerMode(TIMER_MODE_DEFAULT);
        setCps(CPS_DEFAULT);
        setDelay(DELAY_DEFAULT);
        setClickMode(CLICK_MODE_DEFAULT);
        setClickLimit(CLICK_LIMIT_DEFAULT);
        setTimeLimit(TIME_LIMIT_DEFAULT);
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

    public boolean isTimerMode() {
        return timerMode;
    }
    public void setTimerMode(boolean timerMode) {
        if (this.timerMode == timerMode) return;

        boolean old = this.timerMode;
        this.timerMode = timerMode;
        support.firePropertyChange("timerMode",old,timerMode); //notify listeners
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
        if (hotkey == ClickerConfig.NO_KEY_BIND_SET){
            return "None";
        }

        if (hotkey < 0) {
            return "Mouse " + (-hotkey);
        }
        else{
            return NativeKeyEvent.getKeyText(hotkey);
        }
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
        support.firePropertyChange("clickedButton",old, clickedButton); //notify listeners
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

    public int getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(int timeLimit) {
        if (this.timeLimit == timeLimit) return;

        int old = this.timeLimit;
        this.timeLimit = timeLimit;
        support.firePropertyChange("timeLimit",old,timeLimit); //notify listeners
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

    public long getElapsedTime() {
        return elapsedTime.get();
    }
    public void setElapsedTime(long value) {
        elapsedTime.set(value);
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
