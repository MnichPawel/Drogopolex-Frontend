package com.example.drogopolex;

public class DrogopolexEvent {
    private String type;
    private String location;

    public DrogopolexEvent(String typ, String locatio) {
        type = typ;
        location = locatio;
    }

    public String GetType(){
        return type;
    }

    public String GetLocation(){
        return location;
    }
}
