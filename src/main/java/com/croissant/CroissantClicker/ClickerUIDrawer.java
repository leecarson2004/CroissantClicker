package com.croissant.CroissantClicker;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ClickerUIDrawer extends JPanel {

    private final ClickerConfig config;

    //overlay drawer panel
    private JPanel drawerCardContainer;
    private boolean drawerContainerVisible = false;
    //overlay drawer subpanels:
    private JPanel settingsPanel;
    private JPanel saveConfigPanel;
    private JPanel loadConfigPanel;
    //drawer header buttons:
    private JButton settingsButton;
    private JButton saveButton;
    private JButton loadButton;

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

        JPanel drawerContainer = new JPanel(new BorderLayout());
        drawerContainer.setPreferredSize(new Dimension(ClickerConfig.WINDOW_WIDTH/2,ClickerConfig.WINDOW_HEIGHT));
        add(drawerContainer,BorderLayout.WEST);

        JPanel drawerHeaderPanel = buildDrawerHeader();
        drawerContainer.add(drawerHeaderPanel,BorderLayout.NORTH);

        drawerCardContainer = new JPanel(new CardLayout());

        //------------------------------------------------------------------------------
        settingsPanel = new JPanel(new BorderLayout());


        JPanel settingsPanelMain = new JPanel();



        settingsPanel.add(settingsPanelMain, BorderLayout.SOUTH);

        //------------------------------------------------------------------------------
        saveConfigPanel = new JPanel(new BorderLayout());


        JPanel saveConfigPanelMain = new JPanel();



        saveConfigPanel.add(saveConfigPanelMain, BorderLayout.SOUTH);

        //------------------------------------------------------------------------------
        loadConfigPanel = new JPanel(new BorderLayout());


        JPanel loadConfigPanelMain = new JPanel();



        loadConfigPanel.add(loadConfigPanelMain, BorderLayout.SOUTH);

        //------------------------------------------------------------------------------
        drawerCardContainer.add(settingsPanel, "Settings");
        drawerCardContainer.add(saveConfigPanel, "Save");
        drawerCardContainer.add(loadConfigPanel, "Load");

        drawerContainer.add(drawerCardContainer, BorderLayout.SOUTH);
    }

    public void showSelectedDrawerPanel(String panelName) {
        if (!drawerContainerVisible){
            toggleDrawerVisible();
        }
        CardLayout cardLayout = (CardLayout) drawerCardContainer.getLayout();
        cardLayout.show(drawerCardContainer,panelName);
    }

    private void closeDrawer(){
        toggleDrawerVisible();
    }

    //TODO: add a method which will take string input and then change selected status for given button (style should also be improved), call from ONLY showSelectedDrawerPanel aswell
    private JPanel buildDrawerHeader(){
        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(new MigLayout(
                "fill, insets 10 15 10 15",
                "[left][left][left][grow,right]"
        ));

        loadButton = new JButton("⇑");
        setHeaderStyle(loadButton, "Load");
        loadButton.addActionListener(_ -> {
            showSelectedDrawerPanel("Load");
            setStyleUnselected(saveButton);
            setStyleSelected(loadButton);
            setStyleUnselected(settingsButton);
        });

        saveButton = new JButton("⇓");
        setHeaderStyle(saveButton, "Save");
        saveButton.addActionListener(_ -> {
            showSelectedDrawerPanel("Save");
            setStyleSelected(saveButton);
            setStyleUnselected(loadButton);
            setStyleUnselected(settingsButton);
        });

        settingsButton = new JButton("⚙");
        setHeaderStyle(settingsButton, "Settings");
        settingsButton.addActionListener(_ -> {
            showSelectedDrawerPanel("Settings");
            setStyleUnselected(saveButton);
            setStyleUnselected(loadButton);
            setStyleSelected(settingsButton);
        });

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

    private void toggleDrawerVisible(){
        drawerContainerVisible = !drawerContainerVisible;
        setVisible(drawerContainerVisible);
    }

    private void setStyleSelected(JButton button){
        button.putClientProperty("JButton.buttonType", "borderless");
    }
    private void setStyleUnselected(JButton button){
        button.putClientProperty("JButton.buttonType", "square");
    }

    private void setHeaderStyle(JButton button, String tooltip){
        button.putClientProperty("JButton.buttonType", "square");
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 16f));
        button.setToolTipText(tooltip);
    }
}
