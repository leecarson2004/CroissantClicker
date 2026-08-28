package com.croissant.CroissantClicker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class SaveDataManager {
    private static final String APP_NAME = "CroissantClicker";

    private static Path getSaveDirectory() throws IOException{
        String os = System.getProperty("os.name").toLowerCase();
        Path saveDir;

        if (os.contains("win")){
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData == null) {
                localAppData = System.getProperty("user.home");
            }
            saveDir = Paths.get(localAppData, APP_NAME, "saves");
        }else if (os.contains("mac")) {
            saveDir = Paths.get(System.getProperty("user.home"), "Library", "Application Support", APP_NAME, "saves");
        } else{
            saveDir = Paths.get(System.getProperty("user.home"), "." + APP_NAME.toLowerCase(), "saves");
        }
        //if directory doesn't exist, create directory.
        Files.createDirectories(saveDir);

        return saveDir;
    }

    private static Path getSaveFilePath(String configName) throws IOException{
        Path saveDir = getSaveDirectory();
        return saveDir.resolve(createFileName(configName));
    }

    public static void save(ClickerConfig config, String configName){
        try{
            Path filePath = getSaveFilePath(configName);

            //save new config data into properties
            Properties configProps = new Properties();
            configProps.setProperty("configName", configName);
            configProps.setProperty("version",ClickerConfig.APP_VERSION);

            configProps.setProperty("hotkey", String.valueOf(config.getHotkey()));
            configProps.setProperty("delayMode", String.valueOf(config.isDelayMode()));
            configProps.setProperty("timerMode", String.valueOf(config.isTimerMode()));
            configProps.setProperty("delay", String.valueOf(config.getDelay()));
            configProps.setProperty("cps",String.valueOf(config.getCps()));
            configProps.setProperty("clickLimit",String.valueOf(config.getClickLimit()));
            configProps.setProperty("timeLimit",String.valueOf(config.getTimeLimit()));
            configProps.setProperty("clickMode",String.valueOf(config.getClickMode()));
            configProps.setProperty("clickedButton",String.valueOf(config.getClickedButton()));
            configProps.setProperty("theme", config.getTheme());

            try (OutputStream output = Files.newOutputStream(filePath)) {
                configProps.store(output, "User Save Data Configuration: " + configName);
            }

            }catch (IOException e){
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public static void load(ClickerConfig config, String configName) {
        try {
            Path filePath = getSaveFilePath(configName);

            if (!Files.exists(filePath)) {
                return; //use defaults already in ClickerConfig
            }

            Properties configProps = new Properties();

            try (InputStream input = Files.newInputStream(filePath)) {
                configProps.load(input);

                //load config values by keys and store in clickerconfig
                config.setHotkey(parseIntSafe(configProps.getProperty("hotkey"),
                        ClickerConfig.HOTKEY_DEFAULT));
                config.setDelayMode(Boolean.parseBoolean(configProps.getProperty("delayMode",
                        String.valueOf(ClickerConfig.DELAY_MODE_DEFAULT))));
                config.setTimerMode(Boolean.parseBoolean(configProps.getProperty("timerMode",
                        String.valueOf(ClickerConfig.TIMER_MODE_DEFAULT))));
                config.setDelay(parseIntSafe(configProps.getProperty("delay"),
                        ClickerConfig.DELAY_DEFAULT));
                config.setCps(parseIntSafe(configProps.getProperty("cps"),
                        ClickerConfig.CPS_DEFAULT));
                config.setClickLimit(parseIntSafe(configProps.getProperty("clickLimit"),
                        ClickerConfig.CLICK_LIMIT_DEFAULT));
                config.setTimeLimit(parseIntSafe(configProps.getProperty("timeLimit"),
                        ClickerConfig.TIME_LIMIT_DEFAULT));
                config.setClickMode(configProps.getProperty("clickMode",
                        ClickerConfig.CLICK_MODE_DEFAULT));
                config.setClickedButton(parseIntSafe(configProps.getProperty("clickedButton"),
                        ClickerConfig.CLICKED_BUTTON_DEFAULT));
                config.setTheme(configProps.getProperty("theme", ClickerConfig.THEME_DEFAULT));

            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    public static void toggleFavorite(String configName, boolean isFavorite){
        try{
            Path filePath = getSaveFilePath(configName);

            if (!Files.exists(filePath)) {
                System.err.println("No file with name: " + configName + " located to favorite.");
                return;
            }

            Properties configProps = new Properties();

            try (InputStream input = Files.newInputStream(filePath)) {
                configProps.load(input);
            } catch (IOException e) {
                System.err.println("Error loading config: " + e.getMessage());
            }

            configProps.setProperty("favorite", String.valueOf(isFavorite));

        }catch(IOException e) {
            System.err.println("Error while favouring config: " + e.getMessage());
        }
    }

    public static Map<String, Boolean> loadAllConfigNames(){

        //treemap auto sorts by key
        Map<String, Boolean> loadedConfigs = new TreeMap<>();
        Properties configProps = new Properties();

        try {
            Path saveDir = getSaveDirectory();
            File[] configFiles = saveDir.toFile().listFiles((_, name) -> name.endsWith(".properties"));

            if (configFiles == null || configFiles.length == 0){ //no stored save data
                return new TreeMap<>();
            }

            //load all config names into array
            for (File configFile : configFiles){
                Path filePath = saveDir.resolve(configFile.getName());

                try (InputStream input = Files.newInputStream(filePath)){
                    configProps.load(input);

                    String configName = configProps.getProperty("configName","NAME_MISSING");
                    Boolean isFavorite = Boolean.parseBoolean(configProps.getProperty("isFavorite", String.valueOf(false)));

                    loadedConfigs.put(configName, isFavorite);

                } catch (IOException e){
                    System.err.println("Failed to load configFile " + configFile.getName() + ": " + e.getMessage());
                }
            }

            return loadedConfigs;

        } catch (IOException e) {
            System.err.println("Error loading all config template names: " + e.getMessage());
            return new TreeMap<>();
        }
    }

    public static void delete(String configName){
        try{
            Path saveDir = getSaveDirectory();
            Path filePath = saveDir.resolve(createFileName(configName));

            Files.deleteIfExists(filePath);

        } catch(IOException e) {
            System.err.println("Error deleting config: " + e.getMessage());
        }
    }

    private static String createFileName(String configName) {
        return "config." + getFileFormattedName(configName) + ".properties";
    }
    
    private static String getFileFormattedName(String configName){
        return configName.replaceAll(" ", "_");
    }

    private static int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}