package com.example.drogopolex.data.network.request;

public class AvoidPointRequest {
    double longitude;
    double latitude;
    int radius;

    public AvoidPointRequest(double latitude, double longitude, int radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }
}
