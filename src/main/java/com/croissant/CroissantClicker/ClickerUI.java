package com.croissant.CroissantClicker;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;


public class ClickerUI extends JFrame {

    private final ClickerConfig config;
    private final ClickerLogic logic;
    private final ClickerUIDrawer drawer;

    //Timer updating click count 20 times/sec in UI
    private final Timer clickCountRefreshTimer = new Timer(50, _->updateClickCount());
    //Timer for toggle count down when toggle button is clicked
    private Timer toggleCountDownTimer;
    private int countdown;

    private JLabel hotKeyLabel;
    private JLabel clickCounterLabel;
    private JLabel toggleIndicator;
    private JSpinner cpsSpinner;
    private JSpinner delaySpinner;
    private JPanel delayTypePanel;
    private JSpinner clickLimitSpinner;
    private KeyBindTextField clickedButtonSelector;
    private JComboBox<String> clickModeSelector;
    private JButton toggleIndicatorButton;

    String colorGreen = "#388e3c";
    String colorRed = "#d32f2f";


    public ClickerUI(ClickerConfig config, ClickerLogic logic) {
        this.config = config;
        this.logic = logic;

        drawer = new ClickerUIDrawer(config);
        //glass pane consumes user mouse clicks so they don't leak to UI under drawer when open
        setGlassPane(drawer);

        //listen for config changes
        config.addPropertyChangeListener(evt -> {
            //update UI on swing thread:
            SwingUtilities.invokeLater(()->{
                if ("enabled".equals(evt.getPropertyName())){
                    updateStatus();
                }
                else{
                    refreshData(evt);
                }
            });
        });

        initUI();
    }

    private void refreshData(PropertyChangeEvent evt) {
        config.setUpdatingFromConfig(true);

        if ("cps".equals(evt.getPropertyName())){
            cpsSpinner.setValue(evt.getNewValue());
        }
        else if ("delay".equals(evt.getPropertyName())){
            delaySpinner.setValue(evt.getNewValue());
        }
        else if ("delayMode".equals(evt.getPropertyName())){
            boolean isDelayMode = (boolean) evt.getNewValue();

            drawer.setDisplayedDelayMode(isDelayMode);
            CardLayout cardLayout = (CardLayout) delayTypePanel.getLayout();
            if (isDelayMode){
                cardLayout.show(delayTypePanel, "delay");
            } else{
                cardLayout.show(delayTypePanel, "cps");
            }
        }
        else if ("clickLimit".equals(evt.getPropertyName())){
            clickLimitSpinner.setValue(evt.getNewValue());
        }
        else if ("clickedButton".equals(evt.getPropertyName())){
            int clickedButton = (int) evt.getNewValue();

            clickedButtonSelector.setKeyBind(clickedButton);
        }
        else if ("clickMode".equals(evt.getPropertyName())){
            String clickMode = evt.getNewValue().toString();

            clickLimitSpinner.setEnabled(!clickMode.equals("Hold") && !clickMode.equals("Unlimited Clicks"));
            cpsSpinner.setEnabled(!clickMode.equals("Hold"));
            delaySpinner.setEnabled(!clickMode.equals("Hold"));


            clickModeSelector.setSelectedItem(clickMode);
        }
        else if ("theme".equals(evt.getPropertyName())){
            String theme = (String) evt.getNewValue();

            drawer.setDisplayedTheme(theme);
            ThemeManager.setTheme(theme, this);
        }
        else if ("hotkey".equals(evt.getPropertyName())){
            String hotkeyString = config.getHotkeyString();

            drawer.setDisplayedHotkey(hotkeyString);
            hotKeyLabel.setText("[" + hotkeyString + "]");
        }
        else{
            System.err.println("Event name non-existent!");
        }

        config.setUpdatingFromConfig(false);
    }

    private void updateStatus(){
        //turn on clicker
        if (config.isEnabled()){
            toggleIndicator.putClientProperty("FlatLaf.style", "foreground: " + colorGreen);
            toggleIndicatorButton.setText("ON");

            //ensure input valid
            if (!commitAndValidateSpinnerInput()){
                config.setEnabled(false);
                return;
            }

            clickCountRefreshTimer.start();

            try {
                logic.start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Failed to start autoclicker logic: " + e.getMessage());
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
        boolean isInputValid = true;

        if(!(commitAndValidateSpinnerInputHelper(cpsSpinner,
                config.getCps(),
                ClickerConfig.CPS_MIN,
                ClickerConfig.CPS_MAX))) {
            isInputValid = false;
        }
        else if (!(commitAndValidateSpinnerInputHelper(clickLimitSpinner,
                config.getClickLimit(),
                ClickerConfig.CLICK_LIMIT_MIN,
                ClickerConfig.CLICK_LIMIT_MAX))){
            isInputValid = false;
        }
        else if (!(commitAndValidateSpinnerInputHelper(delaySpinner,
                config.getDelay(),
                ClickerConfig.DELAY_MIN,
                ClickerConfig.DELAY_MAX))){
            isInputValid = false;
        }

        return isInputValid;
    }

    private boolean commitAndValidateSpinnerInputHelper(JSpinner spinner, int currValue, int minValue, int maxValue){

        JFormattedTextField spinnerTextField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();

        try {
            spinner.commitEdit();

        } catch (ParseException e) {
            System.err.println("Parse Exception while validating spinner input: " + e.getMessage());

            int value = -1;

            try {
                value = Integer.parseInt(spinnerTextField.getText());
            } catch (NumberFormatException ex){
                System.err.println("Number Format Exception while parsing spinner input: " + ex.getMessage());
            }

            if (value < minValue) {
                spinnerTextField.setValue(minValue);
                spawnSpinnerInputError(spinner, minValue, maxValue);
                return false;
            } else if (value > maxValue) {
                spinnerTextField.setValue(maxValue);
                spawnSpinnerInputError(spinner, minValue, maxValue);
                return false;
            } else{
                spinnerTextField.setValue(currValue);
                spawnSpinnerInputError(spinner,minValue,maxValue);
                return false;
            }
        }

        spinner.putClientProperty("JComponent.outline", null);
        spinnerTextField.setToolTipText(null);
        return true;
    }

    private void spawnSpinnerInputError(JSpinner spinner, int minValue, int maxValue){
        spinner.putClientProperty("JComponent.outline", "error");
        spinner.setToolTipText("Input must be between " + minValue + " and " + maxValue);
    }


    private void initUI() {
        setTitle("Croissant Clicker v" + ClickerConfig.APP_VERSION);
        setSize(ClickerConfig.WINDOW_WIDTH, ClickerConfig.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        //-----------------------------------------------------------------------------
        JPanel defaultPageContainer = new JPanel();
        defaultPageContainer.setLayout(new BorderLayout()); //splits screen into 5 areas -- north,south,center,east,west
        setContentPane(defaultPageContainer);

        //-----------------------------------------------------------------------------
        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(new MigLayout(
                "fill, insets 10 15 10 15",
                "[left][left][left][left][grow,right][right]"
        ));

        JButton loadButton = new JButton("⇑");
        setHeaderStyle(loadButton, "Load");
        loadButton.addActionListener(_ -> drawer.showSelectedDrawerPanel("Load"));

        JButton saveButton = new JButton("⇓");
        setHeaderStyle(saveButton, "Save");
        saveButton.addActionListener(_ -> drawer.showSelectedDrawerPanel("Save"));

        JButton settingsButton = new JButton("⚙");
        setHeaderStyle(settingsButton, "Settings");
        settingsButton.addActionListener(_ -> drawer.showSelectedDrawerPanel("Settings"));

        JButton resetConfigButton = new JButton("↻");
        setHeaderStyle(resetConfigButton, "Reset");
        resetConfigButton.addActionListener(_ -> config.setDefaultConfig());

        toggleIndicator = new JLabel("⬤");
        setHeaderStyle(toggleIndicator);
        toggleIndicator.putClientProperty("FlatLaf.style", "foreground: " + colorRed);

        hotKeyLabel = new JLabel("[" + config.getHotkeyString() + "]");
        setHeaderStyle(hotKeyLabel);


        headerPanel.add(new JSeparator(), "dock north, growx");
        headerPanel.add(settingsButton);
        headerPanel.add(loadButton);
        headerPanel.add(saveButton);
        headerPanel.add(resetConfigButton);
        headerPanel.add(hotKeyLabel);
        headerPanel.add(toggleIndicator);
        headerPanel.add(new JSeparator(), "dock south, growx");

        defaultPageContainer.add(headerPanel, BorderLayout.NORTH);
        //-----------------------------------------------------------------------------

        JPanel mainPanel = new JPanel(new BorderLayout());

        //------------------------------------------------------------------------------
        JPanel mainPanelLeft = new JPanel();

        mainPanelLeft.setLayout(new MigLayout(
                "fillx, insets 10 20 20 10, wrap 2",
                "[left]25[fill]",
                "15[]10[]10[]10[]15[]5[]5[]push"
        ));

        JLabel clickLimitLabel = new JLabel("Click Limit:");

        SpinnerNumberModel clickLimitSpinnerModel = new SpinnerNumberModel(config.getClickLimit(), ClickerConfig.CLICK_LIMIT_MIN, ClickerConfig.CLICK_LIMIT_MAX, 1);
        clickLimitSpinner = new JSpinner(clickLimitSpinnerModel);
        setSpinnerFocusLostBehavior(clickLimitSpinner);
        clickLimitSpinner.addChangeListener(_ -> {
            if (!config.isUpdatingFromConfig()) {
                config.setClickLimit((int)clickLimitSpinner.getValue());
            }
        });
        clickLimitSpinner.setEnabled(!config.getClickMode().equals("Hold") && !config.getClickMode().equals("Unlimited Clicks"));


        JPanel cpsPanel = new JPanel(new MigLayout(
                "fillx, insets 0, wrap 2",
                "[left][fill]"
        ));
        JLabel cpsLabel = new JLabel("CPS:");

        SpinnerNumberModel cpsSpinnerModel = new SpinnerNumberModel(config.getCps(), ClickerConfig.CPS_MIN, ClickerConfig.CPS_MAX, 1);
        cpsSpinner = new JSpinner(cpsSpinnerModel);
        setSpinnerFocusLostBehavior(cpsSpinner);
        cpsSpinner.addChangeListener(_ -> {
            if (!config.isUpdatingFromConfig()) {
                config.setCps((int)cpsSpinner.getValue());
            }
        });
        cpsSpinner.setEnabled(!config.getClickMode().equals("Hold"));


        cpsPanel.add(cpsLabel);
        cpsPanel.add(cpsSpinner);

        JPanel delayPanel = new JPanel(new MigLayout(
                "fillx, insets 0, wrap 2",
                "[left][fill]"
        ));
        JLabel delayLabel = new JLabel("Delay (ms):");

        SpinnerNumberModel delaySpinnerModel = new SpinnerNumberModel(config.getDelay(), ClickerConfig.DELAY_MIN, ClickerConfig.DELAY_MAX, 10);
        delaySpinner = new JSpinner(delaySpinnerModel);
        setSpinnerFocusLostBehavior(delaySpinner);
        delaySpinner.addChangeListener(_ -> {
            if (!config.isUpdatingFromConfig()) {
                config.setDelay((int)delaySpinner.getValue());
            }
        });
        delaySpinner.setEnabled(!config.getClickMode().equals("Hold"));


        delayPanel.add(delayLabel);
        delayPanel.add(delaySpinner);

        delayTypePanel = new JPanel(new CardLayout());
        delayTypePanel.add(cpsPanel, "cps");
        delayTypePanel.add(delayPanel, "delay");

        CardLayout cardLayout = (CardLayout) delayTypePanel.getLayout();
        if (config.isDelayMode()){
            cardLayout.show(delayTypePanel, "delay");
        } else{
            cardLayout.show(delayTypePanel, "cps");
        }


        JLabel clickModeLabel = new JLabel("Mode:");

        String[] modeStrings = {"Unlimited Clicks", "Limited Clicks", "Hold"};
        clickModeSelector = new JComboBox<>(modeStrings);
        clickModeSelector.setSelectedItem(config.getClickMode());

        clickModeSelector.addActionListener(_ -> {
            if (!config.isUpdatingFromConfig()) {
                config.setClickMode((String) clickModeSelector.getSelectedItem());
            }
        });

        JLabel mouseButtonLabel = new JLabel("Clicked Key:");

        clickedButtonSelector = new KeyBindTextField(config.getClickedButton(), config);
        clickedButtonSelector.setOnKeyChanged(_ -> {
            if (!config.isUpdatingFromConfig()){
                config.setClickedButton(clickedButtonSelector.getKeyBind());
            }
        });

        mainPanelLeft.add(delayTypePanel, "span 2, growx");

        mainPanelLeft.add(mouseButtonLabel);
        mainPanelLeft.add(clickedButtonSelector);

        mainPanelLeft.add(clickModeLabel);
        mainPanelLeft.add(clickModeSelector);

        mainPanelLeft.add(clickLimitLabel);
        mainPanelLeft.add(clickLimitSpinner);

        mainPanel.add(mainPanelLeft, BorderLayout.WEST);

        //------------------------------------------------------------------------------
        JPanel mainPanelRight = new JPanel();

        mainPanelRight.setLayout(new MigLayout(
                "fill, insets 20 20 20 20"
        ));

        ImageIcon mainImage = UIResources.WYNN_EMERALD_ICON;
        JLabel mainImageLabel = new JLabel(mainImage);

        mainPanelRight.add(mainImageLabel);

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
        toggleIndicatorButton.addActionListener(_ -> countDownAndStartClicker());

        mainPanelSouth.add(new JSeparator(), "growx, span 2");
        mainPanelSouth.add(clickCounterLabel);
        mainPanelSouth.add(toggleIndicatorButton);
        mainPanelSouth.add(new JSeparator(), "growx, span 2");

        mainPanel.add(mainPanelSouth, BorderLayout.SOUTH);

        //------------------------------------------------------------------------------
        defaultPageContainer.add(mainPanel);

        //------------------------------------------------------------------------------
    }

    private void countDownAndStartClicker(){
        toggleIndicatorButton.setEnabled(false);
        if (!config.isEnabled()){

            countdown = 3;
            toggleIndicatorButton.setText(String.valueOf(countdown));

            toggleCountDownTimer = new Timer(1000, _ -> {
                countdown--;

                if (countdown > 0){
                    toggleIndicatorButton.setText(String.valueOf(countdown));
                }
                else{
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
    }

    private void setSpinnerFocusLostBehavior(JSpinner spinner){
        JFormattedTextField textField =
                ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        textField.setFocusLostBehavior(JFormattedTextField.COMMIT);
    }

    private void setHeaderStyle(JButton button, String tooltip){
        button.putClientProperty("JButton.buttonType", "square");
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 16f));
        button.setToolTipText(tooltip);
    }
    private void setHeaderStyle(JLabel label){
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 16f));
    }
}
