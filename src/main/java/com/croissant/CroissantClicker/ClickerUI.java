package com.croissant.CroissantClicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;

public class ClickerUI extends JFrame {

    private final ClickerConfig config;
    private final ClickerLogic logic;

    //Timer updating click count 20 times/sec in UI
    private final Timer clickCountRefreshTimer = new Timer(50, _->updateClickCount());

    JLabel clickCounterLabel;
    JButton toggleIndicator;

    JSpinner cpsSpinner;
    JSpinner clickLimitSpinner;
    JComboBox<String> mouseButtonSelector;
    JComboBox<String> clickModeSelector;



    public ClickerUI(ClickerConfig config, ClickerLogic logic) {
        this.config = config;
        this.logic = logic;

        //listen for config changes
        config.addPropertyChangeListener(evt -> {
            //update UI on swing thread:
            SwingUtilities.invokeLater(()->{
                if ("enabled".equals(evt.getPropertyName())){
                    updateStatus();
                }
                else{
                    refreshInputFields(evt);
                }
            });
        });

        initUI();
    }

    private void refreshInputFields(PropertyChangeEvent evt) {
        //System.out.println("Refreshing ui input fields...");

        if ("cps".equals(evt.getPropertyName())){
            cpsSpinner.setValue(config.getCps());
        }
        else if ("clickLimit".equals(evt.getPropertyName())){
            clickLimitSpinner.setValue(config.getClickLimit());
        }
        else if ("mouseButton".equals(evt.getPropertyName())){
            if (config.getMouseButton() == InputEvent.BUTTON1_DOWN_MASK) mouseButtonSelector.setSelectedIndex(0);
            else mouseButtonSelector.setSelectedIndex(1);
        }
        else if ("clickLimitMode".equals(evt.getPropertyName())){
            if (!config.isClickLimitMode()) clickModeSelector.setSelectedIndex(0);
            else clickModeSelector.setSelectedIndex(1);
        }
    }

    private void updateStatus(){
        //turn on clicker
        if (config.isEnabled()){
            toggleIndicator.setText("ON");
            toggleIndicator.setBackground(Color.GREEN);

            commitAndValidateSpinnerInput();
            clickCountRefreshTimer.start();

            try {
                logic.start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                config.setEnabled(false);
            }
        }
        //turn off clicker
        else {
            toggleIndicator.setText("OFF");
            toggleIndicator.setBackground(Color.RED);

            logic.stop();

            clickCountRefreshTimer.stop();
            updateClickCount(); //ensure clickCounter stops on correct final count
        }
    }

    private void updateClickCount() {
        clickCounterLabel.setText("CLICK COUNT: " + config.getClickCount());
    }

    //ensure any manually typed user input in spinners is updated in config
    private void commitAndValidateSpinnerInput() {
        commitAndValidateSpinnerInputHelper(cpsSpinner, config.getCps(), ClickerConfig.CPS_MIN, ClickerConfig.CPS_MAX);
        commitAndValidateSpinnerInputHelper(clickLimitSpinner, config.getClickLimit(), ClickerConfig.CLICK_LIMIT_MIN, ClickerConfig.CLICK_LIMIT_MAX);
    }

    private void commitAndValidateSpinnerInputHelper(JSpinner spinner, int currValue, int minValue, int maxValue){
        try{
            //Test for valid input: Retrieve user typed value:
            JFormattedTextField userInputField = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
            int userInput;
            try{
                userInput = Integer.parseInt(userInputField.getText());
            } catch (NumberFormatException _) {
                userInput = currValue;
            }



            //Clamp input to config bounds and commit updates
            JFormattedTextField spinnerTextField =
                    ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();

            if (userInput < minValue) {
                spinner.setValue(minValue);
                spinnerTextField.setText(String.valueOf(minValue));
            }
            else if (userInput > maxValue) {
                spinner.setValue(maxValue);
                spinnerTextField.setText(String.valueOf(maxValue));
            }
            else{
                spinner.commitEdit();
            }

        }catch(java.text.ParseException _) {}
    }

    private void initUI() {

        //JFrame setup (window)
        setTitle("CroissantClicker");
        setSize(340,250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits when close button clicked
        setLocationRelativeTo(null); // window centered on screen by default with null
        setResizable(false);

        Color darkerGray = new Color(48,48,48);
        Color lighterGray = new Color(60,60,60);

        //column
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(darkerGray);

        //----------------------------
        JPanel row0 = new JPanel();
        row0.setBackground(darkerGray);

        String[] mouseButtonStrings = {"Left Click", "Right Click"};
        mouseButtonSelector = new JComboBox<>(mouseButtonStrings);
        if (config.getMouseButton() == InputEvent.BUTTON1_DOWN_MASK){
            mouseButtonSelector.setSelectedIndex(0);
        }
        else{
            mouseButtonSelector.setSelectedIndex(1);
        }
        mouseButtonSelector.addActionListener(_ -> {
            if(mouseButtonSelector.getSelectedIndex() == 0){
                config.setMouseButton(InputEvent.BUTTON1_DOWN_MASK);
            }
            else{
                config.setMouseButton(InputEvent.BUTTON3_DOWN_MASK);
            }
        });
        row0.add(mouseButtonSelector, BorderLayout.CENTER);

        String[] modeStrings = {"Unlimited Clicks", "Limited Clicks"};
        clickModeSelector = new JComboBox<>(modeStrings);
        if (!config.isClickLimitMode()){
            clickModeSelector.setSelectedIndex(0);
        }
        else{
            clickModeSelector.setSelectedIndex(1);
        }
        clickModeSelector.addActionListener(_ -> config.setClickLimitMode(clickModeSelector.getSelectedIndex() != 0));
        row0.add(clickModeSelector, BorderLayout.CENTER);

        //----------------------------

        JPanel row1 = new JPanel();
        row1.setBackground(darkerGray);

        JLabel clickLimitInstructionLabel = new JLabel("CLICK LIMIT:");
        clickLimitInstructionLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        clickLimitInstructionLabel.setForeground(Color.lightGray);
        row1.add(clickLimitInstructionLabel,BorderLayout.CENTER);

        SpinnerNumberModel clickLimitSpinnerModel = new SpinnerNumberModel(config.getClickLimit(), ClickerConfig.CLICK_LIMIT_MIN, ClickerConfig.CLICK_LIMIT_MAX, 1);
        clickLimitSpinner = new JSpinner(clickLimitSpinnerModel);
        clickLimitSpinner.setBackground(darkerGray);
        clickLimitSpinner.addChangeListener(_ -> config.setClickLimit((int)clickLimitSpinner.getValue()));
        row1.add(clickLimitSpinner, BorderLayout.CENTER);

        clickCounterLabel = new JLabel("CLICK COUNT: " + config.getClickCount());
        clickCounterLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        clickCounterLabel.setForeground(Color.lightGray);
        row1.add(clickCounterLabel,BorderLayout.CENTER);

        //----------------------------
        JPanel row2 = new JPanel();
        row2.setBackground(lighterGray);

        JLabel hotkeyInstructionLabel = new JLabel("HOTKEY: F8");
        hotkeyInstructionLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        hotkeyInstructionLabel.setForeground(Color.lightGray);
        row2.add(hotkeyInstructionLabel, BorderLayout.CENTER);

        //----------------------------
        JPanel row3 = new JPanel();
        row3.setBackground(lighterGray);

        toggleIndicator = new JButton("OFF");
        toggleIndicator.setFont(new Font("Times New Roman", Font.PLAIN, 40));
        toggleIndicator.setFocusPainted(false); //no border around text
        toggleIndicator.setForeground(Color.black);
        toggleIndicator.setBackground(Color.red);
        toggleIndicator.addActionListener(_ -> config.setEnabled(!config.isEnabled()));
        row3.add(toggleIndicator, BorderLayout.CENTER);

        //----------------------------
        JPanel row4 = new JPanel();
        row4.setBackground(darkerGray);

        JLabel cpsInstructionsLabel = new JLabel("CPS: (1-50)");
        cpsInstructionsLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        cpsInstructionsLabel.setForeground(Color.lightGray);
        row4.add(cpsInstructionsLabel, BorderLayout.CENTER);

        SpinnerNumberModel cpsSpinnerModel = new SpinnerNumberModel(config.getCps(), ClickerConfig.CPS_MIN, ClickerConfig.CPS_MAX, 1);
        cpsSpinner = new JSpinner(cpsSpinnerModel);
        cpsSpinner.setBackground(darkerGray);
        cpsSpinner.addChangeListener(_ -> config.setCps((int)cpsSpinner.getValue()));

        row4.add(cpsSpinner, BorderLayout.CENTER);

        //----------------------------
        column.add(row0);
        column.add(row1);
        column.add(row2);
        column.add(row3);
        column.add(row4);

        add(column, BorderLayout.CENTER); //add window with UI panels of autoclicker
    }
}
