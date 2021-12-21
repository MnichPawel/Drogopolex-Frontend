package com.example.drogopolex.data.network.request;

public class AddEventRequest {
    final String latitude;
    final String longitude;
    final String type;

    public AddEventRequest(String latitude, String longitude, String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }
}
