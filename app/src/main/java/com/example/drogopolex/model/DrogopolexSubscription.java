package com.example.drogopolex.model;

public class DrogopolexSubscription {
    private final Integer id_sub;
    private final String location;
    private Boolean rec;

    public DrogopolexSubscription(Integer id_sub, String location, Boolean rec) {
        this.id_sub = id_sub;
        this.location = location;
        this.rec = rec;
    }

    public Integer getId_sub() {
        return id_sub;
    }

    public String getLocation() {
        return location;
    }

    public Boolean getRec() {
        return rec;
    }

    public void setRec(Boolean rec) {
        this.rec = rec;
    }
}
