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
            //check mode:
            int numRemainingClicks = -1;
            if (config.isClickLimitMode()){
                numRemainingClicks = config.getClickLimit();
            }

            //run clicking loop
            while (running) {
                int mouseButton = config.getMouseButton();
                robot.mousePress(mouseButton);
                robot.mouseRelease(mouseButton);

                int cps = config.getCps();

                try {
                    Thread.sleep(1000/cps); //convert cps to ms of delay
                } catch(InterruptedException e){
                    break;
                }

                config.incrementClickCount();

                //stop clicker if click limit reached
                if (numRemainingClicks != -1){
                    numRemainingClicks--;

                    if (numRemainingClicks <= 0){
                        config.setEnabled(false);
                        break;
                    }
                }
            }
        });

        thread.start();
    }

    public void stop(){
        running = false;
        if (thread != null){
            thread.interrupt(); //wake up thread immediately (even if sleeping) and stop it from running
        }
    }
}




