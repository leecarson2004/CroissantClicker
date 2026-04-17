package com.croissant.CroissantClicker;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ClickerUIDrawer extends JPanel {

    private final ClickerConfig config;

    //overlay drawer panel
    private JPanel drawerCardContainer;
    private boolean drawerContainerVisible = false;
    //drawer components:
    private JLabel titleLabel;
    private JButton loadButton;
    private JButton saveButton;
    private JButton settingsButton;
    //settings components:
    private JComboBox<String> themeSelector;
    private JComboBox<String> delayModeSelector;
    private NativeKeyBindTextField hotKeySelectionField;
    //save components:
    private JButton savePageSaveButton;
    private JTextField saveConfigNameField;
    //load components:
    private JButton loadPageLoadButton;
    private JButton loadPageDeleteButton;
    private JScrollPane loadPageScrollPane;
    private JPanel scrollablePanel;

    private final Map<String, JButton> loadedConfigButtons = new HashMap<>();
    private String selectedConfig = "";
    private String prevSelectedConfig = "";


    public ClickerUIDrawer(ClickerConfig config){
        this.config = config;

        initUIDrawer();
    }

    //draw transparent black background over existing menu:
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //paint children

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    private void initUIDrawer(){

        setLayout(new BorderLayout());
        setVisible(false);
        setOpaque(false);
        setBounds(0,0,ClickerConfig.WINDOW_WIDTH,ClickerConfig.WINDOW_HEIGHT);

        //entire drawer page container
        JPanel drawerContainer = new JPanel(new BorderLayout());
        drawerContainer.setPreferredSize(new Dimension(ClickerConfig.WINDOW_WIDTH/2,ClickerConfig.WINDOW_HEIGHT));
        add(drawerContainer,BorderLayout.WEST);

        //block mouse events as glass pane overlay stopping input from reaching lower layer covered by open drawer.
        addMouseListener(new MouseAdapter() {

            //close drawer if user clicks outside of drawer
            @Override
            public void mousePressed(MouseEvent e) {
                //convert p from glass pane coordinates to drawerContainer coordinates.
                Point p = SwingUtilities.convertPoint(ClickerUIDrawer.this, e.getPoint(), drawerContainer);

                if (!drawerContainer.contains(p)){
                    closeDrawer();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {});

        JPanel drawerHeaderPanel = buildDrawerHeader();
        drawerContainer.add(drawerHeaderPanel,BorderLayout.NORTH);

        //contains swappable drawer card menus
        drawerCardContainer = new JPanel(new CardLayout());

        //------------------------------------------------------------------------------
        //overlay drawer subpanels:
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new MigLayout(
                "fillx, insets 10 10 10 10, wrap 2",
                "[left][fill]",
                "[]10[]10[]"
        ));

        JLabel hotKeyLabel = new JLabel("Hotkey:");

        hotKeySelectionField = new NativeKeyBindTextField(config.getHotkey(), config);
        hotKeySelectionField.setOnKeyChanged(key -> {
            if (!config.isUpdatingFromConfig()){
                config.setHotkey(key);
            }
        });


        JLabel themeLabel = new JLabel("Theme:");

        String[] themeStrings = {"Dark", "Light"};
        themeSelector = new JComboBox<>(themeStrings);
        themeSelector.setSelectedItem(config.getTheme());
        themeSelector.addActionListener(_ -> {
            if (!config.isUpdatingFromConfig()){
                config.setTheme((String)themeSelector.getSelectedItem());
            }
        });
        themeSelector.setLightWeightPopupEnabled(false); //fixes cross device issues with glass pane


        JLabel delayModeLabel = new JLabel("Delay Mode:");

        String[] delayModeStrings = {"CPS", "Delay"};
        delayModeSelector = new JComboBox<>(delayModeStrings);
        if (config.isDelayMode()){
            delayModeSelector.setSelectedItem("Delay");
        } else{
            delayModeSelector.setSelectedItem("CPS");
        }
        delayModeSelector.addActionListener(_ -> {
            if (!config.isUpdatingFromConfig()){
                boolean isDelayMode = (delayModeSelector.getSelectedIndex() != 0);
                config.setDelayMode(isDelayMode);
            }
        });
        delayModeSelector.setLightWeightPopupEnabled(false); //fixes cross device issues with glass pane


        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(_ -> closeDrawer());

        settingsPanel.add(hotKeyLabel);
        settingsPanel.add(hotKeySelectionField);
        settingsPanel.add(themeLabel);
        settingsPanel.add(themeSelector);
        settingsPanel.add(delayModeLabel);
        settingsPanel.add(delayModeSelector);

        settingsPanel.add(new JPanel(), "span 2, pushy");
        settingsPanel.add(new JSeparator(), "growx, span 2");
        settingsPanel.add(doneButton, "span 2, align right");

        //------------------------------------------------------------------------------
        JPanel saveConfigPanel = new JPanel();
        saveConfigPanel.setLayout(new MigLayout(
                "insets 10 10 20 10, wrap 2, align center",
                "",
                "10[]10[][]10[]"
        ));

        JLabel saveInstructionLabel = new JLabel("Enter Configuration Name:");

        saveConfigNameField = new JTextField();
        saveConfigNameField.setDocument(new TextFieldLimit(20));

        savePageSaveButton = new JButton("Save");
        savePageSaveButton.addActionListener(_ -> {
            String inputText = saveConfigNameField.getText();

            if (inputText.isEmpty()) {
                showTempSaveFeedback("error", "Please enter a name!");
            } else{
                SaveDataManager.save(config, inputText);
                showTempSaveFeedback("success", "Configuration Saved!");
            }
        });

        JButton savePageDoneButton = new JButton("Done");
        savePageDoneButton.addActionListener(_ -> closeDrawer());

        saveConfigPanel.add(saveInstructionLabel, "span 2");
        saveConfigPanel.add(saveConfigNameField, "span 2, grow");
        saveConfigPanel.add(new JPanel(), "span 2, pushy");
        saveConfigPanel.add(new JSeparator(), "growx, span 2");
        saveConfigPanel.add(savePageSaveButton);
        saveConfigPanel.add(savePageDoneButton);

        //------------------------------------------------------------------------------
        JPanel loadConfigPanel = new JPanel();
        loadConfigPanel.setLayout(new MigLayout(
                "insets 10 10 20 10, wrap 2, fillx",
                "",
                "[grow]10[]10[]"
        ));

        loadPageScrollPane = new JScrollPane();
        loadPageScrollPane.getVerticalScrollBar().setUnitIncrement(10);

        loadPageScrollPane.setBorder(BorderFactory.createEmptyBorder());

        loadPageLoadButton = new JButton("Load");
        loadPageLoadButton.addActionListener(_ -> {
            SaveDataManager.load(config, selectedConfig);
            closeDrawer();
        });

        loadPageDeleteButton = new JButton("Delete");
        loadPageDeleteButton.addActionListener(_ -> {

            ImageIcon icon = UIResources.ARE_YOU_SURE_ICON;

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "ARE YOU SURE \n" +
                            "you want to delete \n"
                            + selectedConfig + "?",
                    "",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    icon
            );

            if (result == JOptionPane.YES_OPTION){
                SaveDataManager.delete(selectedConfig);

                resetLoadPage();
                refreshSavedConfigs();
            }
        });

        buildSavedConfigsPanel();

        loadConfigPanel.add(loadPageScrollPane, "span, grow");
        loadConfigPanel.add(new JSeparator(), "growx, span 2, h 5!");
        loadConfigPanel.add(loadPageLoadButton, "split 2, span 2, center");
        loadConfigPanel.add(loadPageDeleteButton);

        //------------------------------------------------------------------------------
        drawerCardContainer.add(settingsPanel, "Settings");
        drawerCardContainer.add(saveConfigPanel, "Save");
        drawerCardContainer.add(loadConfigPanel, "Load");

        drawerContainer.add(drawerCardContainer, BorderLayout.CENTER);
    }

    private void buildSavedConfigsPanel() {
        scrollablePanel = new JPanel(new MigLayout(
                "insets 5 5 5 5, fillx",
                "fill"
        ));

        loadPageScrollPane.setViewportView(scrollablePanel);
        refreshSavedConfigs();
    }

    private void refreshSavedConfigs() {

        scrollablePanel.removeAll();
        loadedConfigButtons.clear();

        //current config is for the currently loaded config -- don't display in load menu
        ArrayList<String> savedConfigs = SaveDataManager.loadAllConfigTemplateNames();
        savedConfigs.remove("_current");

        if (savedConfigs.isEmpty()){
            JLabel emptyConfigsLabel = new JLabel("No saved configurations yet!");
            emptyConfigsLabel.putClientProperty("FlatLaf.style",
                    "foreground: $Label.disabledForeground"
            );

            scrollablePanel.add(emptyConfigsLabel);
        }
        else{
            int scrollPaneWidth = loadPageScrollPane.getViewport().getWidth();

            for (String configName : savedConfigs){
                JButton configNameButton;

                if (configName.length() == 1){
                    configNameButton = new JButton(configName + " ");
                } else{
                    configNameButton = new JButton(configName);
                }

                configNameButton.setHorizontalAlignment(SwingConstants.LEFT);
                configNameButton.setMaximumSize(new Dimension(scrollPaneWidth-10, Integer.MAX_VALUE));

                configNameButton.addActionListener(_ -> {
                    selectedConfig = configName;
                    updateSavedConfigButtonSelection();
                });

                loadedConfigButtons.put(configName, configNameButton);
                scrollablePanel.add(configNameButton, "span");
            }
        }

        scrollablePanel.revalidate(); //re-run layout manager after original components removed
        scrollablePanel.repaint();
    }

    private void updateSavedConfigButtonSelection() {

        loadPageLoadButton.setEnabled(true);
        loadPageDeleteButton.setEnabled(true);

        JButton selectedButton = loadedConfigButtons.get(selectedConfig);
        if (selectedButton != null){
            setStyleSelected(selectedButton);
        }

        if (!(prevSelectedConfig.isEmpty())){
            JButton prevSelectedButton = loadedConfigButtons.get(prevSelectedConfig);
            if (prevSelectedButton != null){
                setStyleUnselected(prevSelectedButton);
            }
        }
        prevSelectedConfig = selectedConfig;
    }

    private void resetLoadPage(){
        loadPageLoadButton.setEnabled(false);
        loadPageDeleteButton.setEnabled(false);
        selectedConfig = "";
        prevSelectedConfig = "";
    }

    private void showTempSaveFeedback(String outline, String tooltip){
        savePageSaveButton.setEnabled(false);
        saveConfigNameField.putClientProperty("JComponent.outline", outline);
        saveConfigNameField.setToolTipText(tooltip);

        Timer tempTimer = new Timer(2000, _ -> {
            saveConfigNameField.putClientProperty("JComponent.outline", null);
            saveConfigNameField.setToolTipText(null);
            savePageSaveButton.setEnabled(true);
        });

        tempTimer.setRepeats(false);
        tempTimer.start();
    }

    private void resetSaveConfigPanel() {
        saveConfigNameField.putClientProperty("JComponent.outline", null);
        saveConfigNameField.setToolTipText(null);
        saveConfigNameField.setText("");
    }

    public void showSelectedDrawerPanel(String panelName) {
        resetSaveConfigPanel();

        if (!drawerContainerVisible){
            toggleDrawerVisible();
        }
        CardLayout cardLayout = (CardLayout) drawerCardContainer.getLayout();
        cardLayout.show(drawerCardContainer,panelName);

        //refresh loaded configs
        if (panelName.equals("Load")){
            resetLoadPage();
            refreshSavedConfigs();
        }

        setDrawerTitle(panelName);
        setPanelButtonSelected(panelName);
    }

    private void setDrawerTitle(String panelName){
        titleLabel.setText(panelName + ":");
    }

    private void setPanelButtonSelected(String panelName){

        switch (panelName){
            case "Save":
                setStyleSelected(saveButton);
                setStyleUnselected(loadButton);
                setStyleUnselected(settingsButton);
                break;

            case "Load":
                setStyleUnselected(saveButton);
                setStyleSelected(loadButton);
                setStyleUnselected(settingsButton);
                break;

            default: //settings
                setStyleUnselected(saveButton);
                setStyleUnselected(loadButton);
                setStyleSelected(settingsButton);
        }

    }

    private void setStyleSelected(JButton button){
        button.putClientProperty("FlatLaf.style",
                "background: darken($Button.background,8%)"
        );
    }
    private void setStyleUnselected(JButton button){
        button.putClientProperty("FlatLaf.style", null);
    }

    private void closeDrawer(){
        resetSaveConfigPanel();
        toggleDrawerVisible();
    }

    private JPanel buildDrawerHeader(){
        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(new MigLayout(
                "fill, insets 10 10 10 10",
                "[left][grow, right][right][right]"
        ));

        titleLabel = new JLabel("Drawer");
        setHeaderStyle(titleLabel);

        loadButton = new JButton("⇑");
        setHeaderStyle(loadButton, "Load");
        loadButton.addActionListener(_ -> showSelectedDrawerPanel("Load"));

        saveButton = new JButton("⇓");
        setHeaderStyle(saveButton, "Save");
        saveButton.addActionListener(_ -> showSelectedDrawerPanel("Save"));

        settingsButton = new JButton("⚙");
        setHeaderStyle(settingsButton, "Settings");
        settingsButton.addActionListener(_ -> showSelectedDrawerPanel("Settings"));



        headerPanel.add(new JSeparator(), "dock north, growx");
        headerPanel.add(titleLabel);
        headerPanel.add(settingsButton);
        headerPanel.add(loadButton);
        headerPanel.add(saveButton);
        headerPanel.add(new JSeparator(), "dock south, growx");

        return headerPanel;
    }

    private void setHeaderStyle(JButton button, String tooltip){
        button.putClientProperty("JButton.buttonType", "square");
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 14f));
        button.setToolTipText(tooltip);
    }
    private void setHeaderStyle(JLabel label){
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 14f));
    }

    private void toggleDrawerVisible(){
        drawerContainerVisible = !drawerContainerVisible;
        setVisible(drawerContainerVisible);
    }

    public void setDisplayedTheme(String theme){
        themeSelector.setSelectedItem(theme);
    }

    public void setDisplayedDelayMode(boolean delayMode) {
        if (delayMode){
            delayModeSelector.setSelectedItem("Delay");
        } else{
            delayModeSelector.setSelectedItem("CPS");
        }
    }

    public void setDisplayedHotkey(String hotkeyString){
        hotKeySelectionField.setText(hotkeyString);
    }
}