package com.example.drogopolex.model;

public class LocationDetails {
    private final String longitude;
    private final String latitude;

    public LocationDetails(String longitude, String latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
