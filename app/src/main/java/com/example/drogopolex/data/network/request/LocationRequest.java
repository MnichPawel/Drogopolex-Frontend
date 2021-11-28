package com.example.drogopolex.data.network.request;

public class LocationRequest {
    String name;
    Double lat;
    Double lng;

    public LocationRequest(String name, Double lat, Double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public LocationRequest(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LocationRequest(String name) {
        this.name = name;
    }
}
