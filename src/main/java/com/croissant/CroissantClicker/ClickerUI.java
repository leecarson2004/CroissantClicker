package com.croissant.CroissantClicker;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;

public class ClickerUI extends JFrame {

    private final ClickerConfig config;
    private final ClickerLogic logic;

    //Timer updating click count 20 times/sec in UI
    private final Timer clickCountRefreshTimer = new Timer(50, _->updateClickCount());
    //Timer for toggle count down when toggle button is clicked
    private Timer toggleCountDownTimer;
    private int countdown;

    JLabel clickCounterLabel;
    JLabel toggleIndicator;
    JSpinner cpsSpinner;
    JSpinner clickLimitSpinner;
    JComboBox<String> mouseButtonSelector;
    JComboBox<String> clickModeSelector;
    JButton toggleIndicatorButton;

    String colorGreen = "#388e3c";
    String colorRed = "#d32f2f";


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
            toggleIndicator.putClientProperty("FlatLaf.style", "foreground: " + colorGreen);
            toggleIndicatorButton.setText("ON");

            //ensure input valid
            if (!commitAndValidateSpinnerInput()){
                config.setEnabled(false);
                System.out.println("invalid input");
                return;
            }

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
            toggleIndicator.putClientProperty("FlatLaf.style", "foreground: " + colorRed);
            toggleIndicatorButton.setText("OFF");

            logic.stop();

            clickCountRefreshTimer.stop();
            updateClickCount(); //ensure clickCounter stops on correct final count
        }
    }

    private void updateClickCount() {
        clickCounterLabel.setText("Click Count: " + config.getClickCount());
    }

    //ensure any manually typed user input in spinners is updated in config
    private boolean commitAndValidateSpinnerInput() {

        boolean isValidInput1 = commitAndValidateSpinnerInputHelper(cpsSpinner,
                config.getCps(),
                ClickerConfig.CPS_MIN,
                ClickerConfig.CPS_MAX);
        boolean isValidInput2 = commitAndValidateSpinnerInputHelper(clickLimitSpinner,
                config.getClickLimit(),
                ClickerConfig.CLICK_LIMIT_MIN,
                ClickerConfig.CLICK_LIMIT_MAX);

        return (isValidInput1 && isValidInput2);
    }

    private boolean commitAndValidateSpinnerInputHelper(JSpinner spinner, int currValue, int minValue, int maxValue){

        JFormattedTextField spinnerTextField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();

        try{
            //Test for valid input: Retrieve user typed value:
            JFormattedTextField userInputField = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
            int userInput;
            try{
                userInput = Integer.parseInt(userInputField.getText());
            } catch (NumberFormatException _) {
                spinner.setValue(currValue);
                spinnerTextField.setText(String.valueOf(currValue));
                spawnSpinnerInputError(spinner,minValue,maxValue);
                return false;
            }

            //Clamp input to config bounds and commit updates
            if (userInput < minValue) {
                spinner.setValue(minValue);
                spinnerTextField.setText(String.valueOf(minValue));
                spawnSpinnerInputError(spinner,minValue,maxValue);
                return false;

            }
            else if (userInput > maxValue) {
                spinner.setValue(maxValue);
                spinnerTextField.setText(String.valueOf(maxValue));
                spawnSpinnerInputError(spinner,minValue,maxValue);
                return false;
            }
            else{
                spinner.putClientProperty("JComponent.outline", "default");
                spinnerTextField.setToolTipText(null);
                spinner.commitEdit();
                return true;
            }

        }catch(java.text.ParseException _) {
            return false;
        }
    }

    private void spawnSpinnerInputError(JSpinner spinner, int minValue, int maxValue){
        spinner.putClientProperty("JComponent.outline", "error");
        spinner.setToolTipText("Input must be between " + minValue + " and " + maxValue);
    }



    private void initUI() {
        setTitle("Croissant Clicker v" + ClickerConfig.APP_VERSION);
        setSize(400, 290);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout()); //splits screen into 5 areas -- north,south,center,east,west

        //-----------------------------------------------------------------------------
        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(new MigLayout(
                "fill, insets 10 15 10 15",
                "[left][left][left][left][grow,right][right]"
        ));

        JButton loadButton = new JButton("☰");
        loadButton.putClientProperty("JButton.buttonType", "square");
        loadButton.setFont(loadButton.getFont().deriveFont(Font.PLAIN, 16f));
        loadButton.setToolTipText("Load");

        JButton saveButton = new JButton("⇓");
        saveButton.putClientProperty("JButton.buttonType", "square");
        saveButton.setFont(saveButton.getFont().deriveFont(Font.PLAIN, 16f));
        saveButton.setToolTipText("Save");

        toggleIndicator = new JLabel("⬤");
        toggleIndicator.setFont(toggleIndicator.getFont().deriveFont(Font.PLAIN, 16f));
        toggleIndicator.putClientProperty("FlatLaf.style", "foreground: " + colorRed);

        JLabel hotKeyLabel = new JLabel("[F8]");
        hotKeyLabel.setFont(hotKeyLabel.getFont().deriveFont(Font.PLAIN, 16f));

        JButton customizationMenuButton = new JButton("✎");
        customizationMenuButton.putClientProperty("JButton.buttonType", "square");
        customizationMenuButton.setFont(customizationMenuButton.getFont().deriveFont(Font.PLAIN, 16f));
        customizationMenuButton.setToolTipText("Edit Style");

        JButton resetConfigButton = new JButton("↻");
        resetConfigButton.putClientProperty("JButton.buttonType", "square");
        resetConfigButton.setFont(resetConfigButton.getFont().deriveFont(Font.PLAIN, 16f));
        resetConfigButton.setToolTipText("Reset");
        resetConfigButton.addActionListener(_ -> config.setDefaultConfig());

        headerPanel.add(new JSeparator(), "dock north, growx");
        headerPanel.add(loadButton);
        headerPanel.add(saveButton);
        headerPanel.add(customizationMenuButton);
        headerPanel.add(resetConfigButton);
        headerPanel.add(hotKeyLabel);
        headerPanel.add(toggleIndicator);
        headerPanel.add(new JSeparator(), "dock south, growx");

        add(headerPanel, BorderLayout.NORTH);
        //-----------------------------------------------------------------------------

        JPanel mainPanel = new JPanel(new BorderLayout());

        //------------------------------------------------------------------------------
        JPanel mainPanelLeft = new JPanel();

        mainPanelLeft.setLayout(new MigLayout(
                "fillx, insets 10 20 20 10, wrap 2",
                "[left][fill]",
                "15[]10[]10[]10[]15[]5[]5[]push"
        ));

        JLabel clickLimitLabel = new JLabel("Click Limit:");

        SpinnerNumberModel clickLimitSpinnerModel = new SpinnerNumberModel(config.getClickLimit(), ClickerConfig.CLICK_LIMIT_MIN, ClickerConfig.CLICK_LIMIT_MAX, 1);
        clickLimitSpinner = new JSpinner(clickLimitSpinnerModel);
        clickLimitSpinner.addChangeListener(_ -> config.setClickLimit((int)clickLimitSpinner.getValue()));
        JFormattedTextField clickLimitSpinnerTextfield =
                ((JSpinner.DefaultEditor) clickLimitSpinner.getEditor()).getTextField();
        clickLimitSpinnerTextfield.setFocusLostBehavior(JFormattedTextField.COMMIT);

        JLabel cpsLabel = new JLabel("CPS:");

        SpinnerNumberModel cpsSpinnerModel = new SpinnerNumberModel(config.getCps(), ClickerConfig.CPS_MIN, ClickerConfig.CPS_MAX, 1);
        cpsSpinner = new JSpinner(cpsSpinnerModel);
        cpsSpinner.addChangeListener(_ -> config.setCps((int)cpsSpinner.getValue()));
        JFormattedTextField cpsSpinnerTextfield =
                ((JSpinner.DefaultEditor) cpsSpinner.getEditor()).getTextField();
        cpsSpinnerTextfield.setFocusLostBehavior(JFormattedTextField.COMMIT);

        JLabel clickModeLabel = new JLabel("Mode:");

        String[] modeStrings = {"Unlimited Clicks", "Limited Clicks"};
        clickModeSelector = new JComboBox<>(modeStrings);
        if (!config.isClickLimitMode()){
            clickModeSelector.setSelectedIndex(0);
        }
        else{
            clickModeSelector.setSelectedIndex(1);
        }
        clickModeSelector.addActionListener(_ -> config.setClickLimitMode(clickModeSelector.getSelectedIndex() != 0));

        JLabel mouseButtonLabel = new JLabel("Mouse Button:");

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

        mainPanelLeft.add(clickLimitLabel);
        mainPanelLeft.add(clickLimitSpinner);
        mainPanelLeft.add(cpsLabel);
        mainPanelLeft.add(cpsSpinner);
        mainPanelLeft.add(clickModeLabel);
        mainPanelLeft.add(clickModeSelector);
        mainPanelLeft.add(mouseButtonLabel);
        mainPanelLeft.add(mouseButtonSelector);


        mainPanel.add(mainPanelLeft, BorderLayout.WEST);
        //------------------------------------------------------------------------------
        JPanel mainPanelRight = new JPanel();

        mainPanelRight.setLayout(new MigLayout(
                "fillx, insets 10 20 20 10"
        ));

        mainPanel.add(mainPanelRight, BorderLayout.EAST);

        //------------------------------------------------------------------------------
        JPanel mainPanelSouth = new JPanel();

        mainPanelSouth.setLayout(new MigLayout(
                "fillx, insets 10 20 10 20, wrap 2",
                "[left][right]"
        ));

        clickCounterLabel = new JLabel("Click Count: " + config.getClickCount());

        toggleIndicatorButton = new JButton("OFF");
        toggleIndicatorButton.putClientProperty("JButton.buttonType", "roundRect");
        toggleIndicatorButton.addActionListener(_ -> {

            toggleIndicatorButton.setEnabled(false);
            if (!config.isEnabled()){

                countdown = 3;
                toggleIndicatorButton.setText(String.valueOf(countdown));

                toggleCountDownTimer = new Timer(1000, _ -> {
                    countdown--;

                    if (countdown > 0){
                        toggleIndicatorButton.setText(String.valueOf(countdown));
                    } else{
                        toggleCountDownTimer.stop();
                        config.setEnabled(true);
                        toggleIndicatorButton.setEnabled(true);
                    }
                });
                toggleCountDownTimer.start();
            }
            else{
                config.setEnabled(false);
                toggleIndicatorButton.setEnabled(true);
            }
        });

        mainPanelSouth.add(new JSeparator(), "growx, span 2");
        mainPanelSouth.add(clickCounterLabel);
        mainPanelSouth.add(toggleIndicatorButton);
        mainPanelSouth.add(new JSeparator(), "growx, span 2");

        mainPanel.add(mainPanelSouth, BorderLayout.SOUTH);
        //------------------------------------------------------------------------------
        add(mainPanel);

    }
}
