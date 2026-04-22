package com.croissant.CroissantClicker;

public class Keybind {
    private int value;
    private BindType type;

    public Keybind(int value, BindType type){
        this.value = value;
        this.type = type;
    }

    public int getValue(){
        return value;
    }
    public void setValue(int value){
        this.value = value;
    }

    public BindType getType(){
        return type;
    }
    public void setType(BindType type){
        this.type = type;
    }
}
