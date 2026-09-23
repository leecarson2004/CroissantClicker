package com.croissant.CroissantClicker;

public enum ClickMode {
    UNLIMITED("Unlimited Clicks"),
    LIMITED("Limited Clicks"),
    HOLD("Hold");

    private final String name;

    ClickMode(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String[] getNames() {
        return new String[] {UNLIMITED.name, LIMITED.name, HOLD.name};
    }
}
