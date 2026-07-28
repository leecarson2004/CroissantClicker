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
        config.setElapsedTime(0);

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
        long startTime = System.nanoTime();
        long elapsedSeconds = 0;

        if (isTimerMode){
            endTime = startTime + (config.getTimeLimit()*1_000_000_000L);
        }
        else if (clickMode.equals("Limited Clicks")){
            numRemainingClicks = config.getClickLimit();
        }


        long interval = isDelayMode ? delay*1_000_000L : 1_000_000_000L/cps;
        long nextClick = startTime;

        while (running) {
            if (isTimerMode && timeLimitExpired(endTime)){
                break;
            }

            long now = System.nanoTime();

            elapsedSeconds = now - startTime;
            config.setElapsedTime(elapsedSeconds);

            if (now >= nextClick) {
                executeClick(button);
                config.incrementClickCount();

                nextClick += interval;

                //if a program stall occurs, prevent simultaneous clicks.
                if (nextClick < now) {
                    nextClick = now + interval;
                }

                if (numRemainingClicks != -1){
                    numRemainingClicks--;

                    if (clickLimitExpired(numRemainingClicks)){
                        break;
                    }
                }
            } else {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e){
                    break;
                }
            }
        }
    }

    public void startHoldMode(){
        int button = config.getClickedButton();
        boolean isTimerMode = config.isTimerMode();

        long startTime = System.nanoTime();
        long elapsedSeconds = 0;

        long endTime = 0;
        if (isTimerMode){
            endTime = startTime + (config.getTimeLimit()*1_000_000_000L);
        }

        executeHold(button);
        config.incrementClickCount();

        try{
            while (running) {
                Thread.sleep(50);

                elapsedSeconds = System.nanoTime() - startTime;
                config.setElapsedTime(elapsedSeconds);

                if (isTimerMode && timeLimitExpired(endTime)){
                    break;
                }
            }
        } catch (InterruptedException _){
        } finally{
            if (button < 0){
                int maskedButton = InputEvent.getMaskForButton(-button);
                robot.mouseRelease(maskedButton);
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
        if (System.nanoTime() >= endTime){
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




