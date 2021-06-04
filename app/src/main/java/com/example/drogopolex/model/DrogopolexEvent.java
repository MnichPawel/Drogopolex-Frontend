package com.example.drogopolex.model;

public class DrogopolexEvent {
    private String type;
    private String location;
    private int id;

    public DrogopolexEvent(String type, String location, int id) {
        this.type = type;
        this.location = location;
        this.id = id;
    }

    public String getType(){
        return type;
    }

    public String getLocation(){
        return location;
    }

    public int getId(){
        return id;
    }
}
