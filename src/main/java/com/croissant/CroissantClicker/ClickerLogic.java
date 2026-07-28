package com.croissant.CroissantClicker;

import java.awt.*;
import java.awt.event.InputEvent;


public class ClickerLogic {

    private final ClickerConfig config;
    private final Robot robot;
    private volatile boolean running = false;
    private Thread thread;

    public ClickerLogic(ClickerConfig config) throws AWTException {

        this.config = config;
        this.robot = new Robot();

    }

    public void start(){

        running = true;
        config.setClickCount(0);

        thread = new Thread(()->{
            try {
                //check modes:
                String mode = config.getClickMode();

                if (mode.equals("Hold")){
                    startHoldMode();
                }
                else{ //click mode
                    startClickMode(mode);
                }
            } finally {
                thread = null;
            }

        }, "ClickerLogicThread");

        thread.start();
    }

    public void startClickMode(String clickMode){
        //load config settings
        int button = config.getClickedButton();
        int cps = config.getCps();
        int delay = config.getDelay();
        boolean isDelayMode = config.isDelayMode();
        boolean isTimerMode = config.isTimerMode();

        //check click limiters
        int numRemainingClicks = -1;
        long endTime = 0;

        if (isTimerMode){
            endTime = System.nanoTime() + (config.getTimeLimit()*1_000_000_000L);
        }
        else if (clickMode.equals("Limited Clicks")){
            numRemainingClicks = config.getClickLimit();
        }

        int sleepTime = isDelayMode ? delay : 1000/cps;

        while (running) {
            if (isTimerMode && timeLimitExpired(endTime)){
                break;
            }

            executeClick(button);
            config.incrementClickCount();

            if (numRemainingClicks != -1){
                numRemainingClicks--;

                if (clickLimitExpired(numRemainingClicks)){
                    break;
                }
            }

            try {
                Thread.sleep(sleepTime);
            } catch(InterruptedException e){
                break;
            }
        }
    }

    public void startHoldMode(){
        int button = config.getClickedButton();
        boolean isTimerMode = config.isTimerMode();

        long endTime = 0;
        if (isTimerMode){
            endTime = System.nanoTime() + (config.getTimeLimit()*1_000_000_000L);
        }

        executeHold(button);
        config.incrementClickCount();

        try{
            while (running) {
                Thread.sleep(50);

                if (isTimerMode && timeLimitExpired(endTime)){
                    break;
                }
            }
        } catch (InterruptedException _){
        } finally{
            if (button < 0){
                int maskedButton = InputEvent.getMaskForButton(-button);
                robot.mousePress(maskedButton);
            } else{
                robot.keyRelease(button);
            }
        }
    }

    public void stop(){
        running = false;
        if (thread != null){
            thread.interrupt(); //wake up thread immediately (even if sleeping) and stop it from running
        }
    }

    public boolean clickLimitExpired(int numRemainingClicks){
        if (numRemainingClicks <= 0){
            config.setEnabled(false);
            return true;
        }
        return false;
    }

    public boolean timeLimitExpired(long endTime){
        if (System.nanoTime() > endTime){
            config.setEnabled(false);
            return true;
        }
        return false;
    }

    private void executeClick(int button) {
        if (button == ClickerConfig.NO_KEY_BIND_SET) return;

        //mouse
        if (button < 0){
            int maskedButton = InputEvent.getMaskForButton(-button);

            robot.mousePress(maskedButton);
            robot.mouseRelease(maskedButton);
        }
        //keyboard
        else{
            robot.keyPress(button);
            robot.keyRelease(button);
        }
    }

    private void executeHold(int button) {
        if (button == ClickerConfig.NO_KEY_BIND_SET) return;

        //mouse
        if (button < 0){
            int maskedButton = InputEvent.getMaskForButton(-button);
            robot.mousePress(maskedButton);
        }
        //keyboard
        else{
            robot.keyPress(button);
        }
    }
}




