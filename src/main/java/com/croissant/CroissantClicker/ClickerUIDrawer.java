package com.croissant.CroissantClicker;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

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
    private JComboBox<String> themeSelector;
    private JTextField saveConfigNameField;
    //load config selection:
    private String selectedConfig;
    JButton loadPageLoadButton;
    JButton loadPageDeleteButton;

    public ClickerUIDrawer(ClickerConfig config){
        this.config = config;

        initUIDrawer();
    }

    //draw transparent black background over existing menu:
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();

        super.paintComponent(g); //ensure children are then painted on top
    }

    private void initUIDrawer(){

        setLayout(new BorderLayout());
        setOpaque(false);
        setVisible(false);
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
                "[left][fill]"
        ));

        JLabel hotKeyLabel = new JLabel("Hotkey:");

        JLabel ActiveHotKeyLabel = new JLabel("[F8]");

        JLabel themeLabel = new JLabel("Theme:");

        String[] themeStrings = {"Dark", "Light"};
        themeSelector = new JComboBox<>(themeStrings);
        themeSelector.setSelectedItem(config.getTheme());
        themeSelector.addActionListener(_ -> config.setTheme((String)themeSelector.getSelectedItem()));

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(_ -> closeDrawer());

        settingsPanel.add(hotKeyLabel);
        settingsPanel.add(ActiveHotKeyLabel);
        settingsPanel.add(themeLabel);
        settingsPanel.add(themeSelector);
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

        JButton savePageSaveButton = new JButton("Save");
        savePageSaveButton.addActionListener(_ -> {
            String inputText = saveConfigNameField.getText();
            
            if (inputText.isEmpty()) {
                saveConfigNameField.putClientProperty("JComponent.outline", "error");
                saveConfigNameField.setToolTipText("Please enter a name!");
            } else{
                SaveDataManager.save(config, saveConfigNameField.getText());
                closeDrawer();
            }
        });

        JButton savePageCancelButton = new JButton("Cancel");
        savePageCancelButton.addActionListener(_ -> closeDrawer());

        saveConfigPanel.add(saveInstructionLabel, "span 2");
        saveConfigPanel.add(saveConfigNameField, "span 2, grow");
        saveConfigPanel.add(new JPanel(), "span 2, pushy");
        saveConfigPanel.add(new JSeparator(), "growx, span 2");
        saveConfigPanel.add(savePageSaveButton);
        saveConfigPanel.add(savePageCancelButton);

        //------------------------------------------------------------------------------
        JPanel loadConfigPanel = new JPanel();
        loadConfigPanel.setLayout(new MigLayout(
                "insets 10 10 20 10, wrap 3, fillx",
                "",
                "[][]10[]"
        ));

        JScrollPane loadPageScrollPane = new JScrollPane();

        refreshSavedConfigs(loadPageScrollPane);

        loadPageLoadButton = new JButton("Load");
        loadPageLoadButton.setEnabled(false);
        loadPageLoadButton.addActionListener(_ -> SaveDataManager.load(config, selectedConfig));

        JButton loadPageCancelButton = new JButton("◁");
        loadPageCancelButton.putClientProperty("JButton.buttonType", "borderless");
        loadPageCancelButton.setFont(loadPageCancelButton.getFont().deriveFont(Font.PLAIN, 14f));
        loadPageCancelButton.addActionListener(_ -> closeDrawer());

        loadPageDeleteButton = new JButton("⩐");
        loadPageDeleteButton.putClientProperty("JButton.buttonType", "borderless");
        loadPageDeleteButton.setFont(loadPageDeleteButton.getFont().deriveFont(Font.PLAIN, 14f));
        loadPageDeleteButton.setEnabled(false);
        loadPageCancelButton.addActionListener(_ -> SaveDataManager.delete(selectedConfig));

        loadConfigPanel.add(loadPageScrollPane, "span, grow");
        loadConfigPanel.add(new JPanel(), "span, pushy");
        loadConfigPanel.add(loadPageLoadButton, "split 3, span, center");
        loadConfigPanel.add(loadPageDeleteButton);
        loadConfigPanel.add(loadPageCancelButton);

        //------------------------------------------------------------------------------
        drawerCardContainer.add(settingsPanel, "Settings");
        drawerCardContainer.add(saveConfigPanel, "Save");
        drawerCardContainer.add(loadConfigPanel, "Load");

        drawerContainer.add(drawerCardContainer, BorderLayout.CENTER);
    }

    private void refreshSavedConfigs(JScrollPane loadPageScrollPane) {
        JPanel scrollablePanel = new JPanel();
        scrollablePanel.setLayout(new MigLayout(
                "insets 5 5 5 5, fillx",
                "fill"
        ));
        ArrayList<String> savedConfigs = SaveDataManager.loadAllConfigTemplateNames();

        for (String configName : savedConfigs){
            JButton configNameButton = new JButton(configName);
            configNameButton.addActionListener(_ -> {
                selectedConfig = configName;
                setStyleSelected(configNameButton);

                loadPageLoadButton.setEnabled(true);
                loadPageDeleteButton.setEnabled(true);
            });

            scrollablePanel.add(configNameButton, "span");
        }

        loadPageScrollPane.setViewportView(scrollablePanel);
    }

    private void resetSaveConfigPanel() {
        saveConfigNameField.putClientProperty("JComponent.outline", "default");
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

    public void setThemeSelector(String theme){
        themeSelector.setSelectedItem(theme);
    }
}
