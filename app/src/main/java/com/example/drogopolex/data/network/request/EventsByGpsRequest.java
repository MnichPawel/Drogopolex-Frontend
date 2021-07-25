package com.example.drogopolex.data.network.request;

public class EventsByGpsRequest {
    final String latitude;
    final String longitude;

    public EventsByGpsRequest(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
