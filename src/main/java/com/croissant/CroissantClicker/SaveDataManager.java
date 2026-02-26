package com.croissant.CroissantClicker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;


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

    public static void save(ClickerConfig config, String configName){
        try{
            //get save file path
            Path saveDir = getSaveDirectory();
            Path filePath = saveDir.resolve(createFileName(configName));

            //save new config data into properties
            Properties configProps = new Properties();
            configProps.setProperty("Version",ClickerConfig.APP_VERSION);
            configProps.setProperty("cps",String.valueOf(config.getCps()));
            configProps.setProperty("clickLimit",String.valueOf(config.getClickLimit()));
            configProps.setProperty("clickLimitMode",String.valueOf(config.isClickLimitMode()));
            configProps.setProperty("mouseButton",String.valueOf(config.getMouseButton()));

            try (OutputStream output = Files.newOutputStream(filePath)) {
                configProps.store(output, "User Save Data Configuration: " + configName);
            }

            }catch (IOException e){
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public static void load(ClickerConfig config, String configName){
        try{
            //get save directory of savedata
            Path saveDir = getSaveDirectory();
            Path filePath = saveDir.resolve(createFileName(configName));

            if (!Files.exists(filePath)) {
                return; //specified file does not exist, use defaults already in ClickerConfig
            }

            Properties configProps = new Properties();

            try (InputStream input = Files.newInputStream(filePath)){
                configProps.load(input);

                //load config values by keys and store in clickerconfig
                config.setCps(parseIntSafe(configProps.getProperty("cps"),
                        ClickerConfig.CPS_DEFAULT));
                config.setClickLimit(parseIntSafe(configProps.getProperty("clickLimit"),
                        ClickerConfig.CLICK_LIMIT_DEFAULT));
                config.setClickLimitMode(Boolean.parseBoolean(configProps.getProperty("clickLimitMode",String.valueOf(ClickerConfig.CLICK_LIMIT_MODE_DEFAULT))));
                config.setMouseButton(parseIntSafe(configProps.getProperty("mouseButton"),
                        ClickerConfig.MOUSE_BUTTON_DEFAULT));

            }
        } catch(IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    public static ArrayList<String> loadAllConfigTemplateNames(){


        return new ArrayList<>();
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
        return "config." + configName + ".properties";
    }

    private static int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}