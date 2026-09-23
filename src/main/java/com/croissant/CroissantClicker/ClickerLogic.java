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

                if (mode.equals(ClickMode.HOLD.getName())){
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
        long clickLength = config.getClickLength()*1_000_000L;

        boolean isDelayMode = config.isDelayMode();
        boolean isTimerMode = config.isTimerMode() && !clickMode.equals(ClickMode.UNLIMITED.getName());
        boolean isLimitedClicksMode = clickMode.equals(ClickMode.LIMITED.getName()) && !isTimerMode;

        //check click limiters
        int numRemainingClicks = -1;
        long endTime = 0;
        long startTime = System.nanoTime();
        long elapsedTime = 0;

        if (isTimerMode){
            endTime = startTime + (config.getTimeLimit()*1_000_000_000L);
        }
        else if (isLimitedClicksMode){
            numRemainingClicks = config.getClickLimit();
        }



        long interval = isDelayMode ? delay*1_000_000L : 1_000_000_000L/cps;
        long nextClick = startTime;

        while (running) {
            if (isTimerMode && timeLimitExpired(endTime)){
                break;
            }

            long now = System.nanoTime();

            elapsedTime = now - startTime;
            config.setElapsedTime(elapsedTime);

            if (now >= nextClick) {
                pressClick(button);

                long releaseTime = now + clickLength;

                while (running && now < releaseTime) {
                    now = System.nanoTime();

                    elapsedTime = now - startTime;
                    config.setElapsedTime(elapsedTime);

                    try{
                        Thread.sleep(1);
                    } catch (InterruptedException _){
                        break;
                    }
                }

                releaseClick(button);

                config.incrementClickCount();

                nextClick += interval;

                long afterRelease = System.nanoTime();

                //prevent simultaneous clicks due to falling behind or program stall
                if (nextClick < afterRelease) {
                    nextClick = afterRelease + interval;
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

        pressClick(button);
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
            releaseClick(button);
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

    private void pressClick(int button) {
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

    private void releaseClick(int button) {
        if (button == ClickerConfig.NO_KEY_BIND_SET) return;

        //mouse
        if (button < 0){
            int maskedButton = InputEvent.getMaskForButton(-button);
            robot.mouseRelease(maskedButton);
        }
        //keyboard
        else{
            robot.keyRelease(button);
        }
    }
}




