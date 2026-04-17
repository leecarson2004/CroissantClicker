package com.croissant.CroissantClicker;

import java.awt.*;



public class ClickerLogic {

    private final ClickerConfig config;
    private final Robot robot;
    private volatile boolean running = false;
    private Thread thread;

    public ClickerLogic(ClickerConfig config) throws AWTException {

        this.config = config;
        this.robot = new Robot();

    }

    public void start() throws InterruptedException {
        if (thread != null && thread.isAlive()){
            return;
        }

        running = true;
        config.setClickCount(0);

        thread = new Thread(()->{
            //check modes:
            String mode = config.getClickMode();

            if (mode.equals("Hold")){
                startHoldMode();
            }
            //CLICK MODE:
            else{
                startClickMode(mode);
            }
        });

        thread.start();
    }

    public void startClickMode(String clickMode){
        //check click mode:
        int numRemainingClicks = -1;
        if (clickMode.equals("Limited Clicks")){
            numRemainingClicks = config.getClickLimit();
        }

        //load config settings
        int mouseButton = config.getMouseButton();
        int cps = config.getCps();
        int delay = config.getDelay();
        boolean delayMode = config.isDelayMode();

        //Check delay mode & run appropriate clicking loop
        if (delayMode){
            while (running) {
                config.incrementClickCount();

                robot.mousePress(mouseButton);
                robot.mouseRelease(mouseButton);

                //stop clicker if click limit reached
                if (numRemainingClicks != -1){
                    numRemainingClicks--;

                    if (numRemainingClicks <= 0){
                        config.setEnabled(false);
                        break;
                    }
                }

                try {
                    Thread.sleep(delay);
                } catch(InterruptedException e){
                    break;
                }
            }
        }
        //CPS MODE:
        else {
            while (running) {
                config.incrementClickCount();

                robot.mousePress(mouseButton);
                robot.mouseRelease(mouseButton);

                //stop clicker if click limit reached
                if (numRemainingClicks != -1){
                    numRemainingClicks--;

                    if (numRemainingClicks <= 0){
                        config.setEnabled(false);
                        break;
                    }
                }

                try {
                    Thread.sleep(1000/cps); //convert cps to ms of delay
                } catch(InterruptedException e){
                    break;
                }
            }
        }
    }
    public void startHoldMode(){
        int mouseButton = config.getMouseButton();

        robot.mousePress(mouseButton);
        config.incrementClickCount();

        try{
            while (running) {
                Thread.sleep(50);
            }
        } catch (InterruptedException _){
        } finally{
            robot.mouseRelease(mouseButton);
        }

    }

    public void stop(){
        running = false;
        if (thread != null){
            thread.interrupt(); //wake up thread immediately (even if sleeping) and stop it from running
        }
    }
}




