package com.example.drogopolex.model;

public class DrogopolexEvent {
    private String type;
    private String location;

    public DrogopolexEvent(String type, String location) {
        this.type = type;
        this.location = location;
    }

    public String GetType(){
        return type;
    }

    public String GetLocation(){
        return location;
    }
}
