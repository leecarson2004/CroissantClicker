package com.croissant.CroissantClicker;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

public class ClickerUIDrawer extends JPanel {

    private final ClickerConfig config;

    //overlay drawer panel
    private JPanel drawerCardContainer;
    private boolean drawerContainerVisible = false;
    //overlay drawer subpanels:
    private JPanel settingsPanel;
    private JPanel saveConfigPanel;
    private JPanel loadConfigPanel;
    //drawer components:
    private JButton settingsButton;
    private JButton saveButton;
    private JButton loadButton;
    private JComboBox<String> themeSelector;

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

        //block mouse events as glass pane overlay stopping input from reaching lower layer covered by open drawer.
        addMouseListener(new MouseAdapter() {});
        addMouseMotionListener(new MouseMotionAdapter() {});

        JPanel drawerContainer = new JPanel(new BorderLayout());
        drawerContainer.setPreferredSize(new Dimension(ClickerConfig.WINDOW_WIDTH/2,ClickerConfig.WINDOW_HEIGHT));
        add(drawerContainer,BorderLayout.WEST);

        JPanel drawerHeaderPanel = buildDrawerHeader();
        drawerContainer.add(drawerHeaderPanel,BorderLayout.NORTH);

        drawerCardContainer = new JPanel(new CardLayout());

        //------------------------------------------------------------------------------
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new MigLayout(
                "fillx, insets 10 10 20 10, wrap 2",
                "[left][fill]",
                "[][][]10[]"
        ));

        buildDrawerPanelTitle(settingsPanel, "Settings:");

        JLabel hotKeyLabel = new JLabel("Hotkey:");

        JLabel ActiveHotKeyLabel = new JLabel("[F8]");

        JLabel themeLabel = new JLabel("Theme:");

        String[] themeStrings = {"Dark", "Light"};
        themeSelector = new JComboBox<>(themeStrings);
        themeSelector.setSelectedItem(config.getTheme());
        themeSelector.addActionListener(_ -> config.setTheme((String)themeSelector.getSelectedItem()));


        settingsPanel.add(hotKeyLabel);
        settingsPanel.add(ActiveHotKeyLabel);
        settingsPanel.add(themeLabel);
        settingsPanel.add(themeSelector);
        settingsPanel.add(new JPanel(), "span 2, pushy");
        settingsPanel.add(new JSeparator(), "span 2, growx");


        //------------------------------------------------------------------------------
        saveConfigPanel = new JPanel();
        saveConfigPanel.setLayout(new MigLayout(
                "fillx, insets 10 10 10 10, wrap 2",
                "[left][fill]"
        ));

        buildDrawerPanelTitle(saveConfigPanel, "Save Configuration:");

        //------------------------------------------------------------------------------
        loadConfigPanel = new JPanel();
        loadConfigPanel.setLayout(new MigLayout(
                "fillx, insets 10 10 10 10, wrap 2",
                "[left][fill]"
        ));

        buildDrawerPanelTitle(loadConfigPanel, "Load Configuration:");

        //------------------------------------------------------------------------------
        drawerCardContainer.add(settingsPanel, "Settings");
        drawerCardContainer.add(saveConfigPanel, "Save");
        drawerCardContainer.add(loadConfigPanel, "Load");

        drawerContainer.add(drawerCardContainer, BorderLayout.CENTER);
    }

    public void buildDrawerPanelTitle(JPanel drawerPanel, String title){
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 14f));

        drawerPanel.add(titleLabel, "span 2");
        drawerPanel.add(new JSeparator(), "span 2, growx");
    }

    public void showSelectedDrawerPanel(String panelName) {
        if (!drawerContainerVisible){
            toggleDrawerVisible();
        }
        CardLayout cardLayout = (CardLayout) drawerCardContainer.getLayout();
        cardLayout.show(drawerCardContainer,panelName);
        setPanelButtonSelected(panelName);
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
        toggleDrawerVisible();
    }

    private JPanel buildDrawerHeader(){
        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(new MigLayout(
                "fill, insets 10 15 10 15",
                "[left][left][left][grow,right]"
        ));

        loadButton = new JButton("⇑");
        setHeaderStyle(loadButton, "Load");
        loadButton.addActionListener(_ -> showSelectedDrawerPanel("Load"));

        saveButton = new JButton("⇓");
        setHeaderStyle(saveButton, "Save");
        saveButton.addActionListener(_ -> showSelectedDrawerPanel("Save"));

        settingsButton = new JButton("⚙");
        setHeaderStyle(settingsButton, "Settings");
        settingsButton.addActionListener(_ -> showSelectedDrawerPanel("Settings"));

        JButton exitButton = new JButton("◁");
        exitButton.putClientProperty("JButton.buttonType", "borderless");
        exitButton.setFont(exitButton.getFont().deriveFont(Font.PLAIN, 16f));
        exitButton.addActionListener(_ -> closeDrawer());

        headerPanel.add(new JSeparator(), "dock north, growx");
        headerPanel.add(settingsButton);
        headerPanel.add(loadButton);
        headerPanel.add(saveButton);
        headerPanel.add(exitButton);
        headerPanel.add(new JSeparator(), "dock south, growx");

        return headerPanel;
    }
    private void setHeaderStyle(JButton button, String tooltip){
        button.putClientProperty("JButton.buttonType", "square");
        button.setToolTipText(tooltip);
    }

    private void toggleDrawerVisible(){
        drawerContainerVisible = !drawerContainerVisible;
        setVisible(drawerContainerVisible);
    }

    public void setThemeSelector(String theme){
        themeSelector.setSelectedItem(theme);
    }
}
