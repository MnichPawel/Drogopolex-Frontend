package com.example.drogopolex.model;

public class DrogopolexSubscription {
    private Integer id_sub;
    private String location;
    public DrogopolexSubscription(Integer id_sub, String location){
        this.id_sub=id_sub;
        this.location=location;
    }
    public Integer getId_sub(){
        return id_sub;
    }

    public String getLocation(){
        return location;
    }
}
