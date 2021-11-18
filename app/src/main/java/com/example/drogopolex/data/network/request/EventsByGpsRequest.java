package com.example.drogopolex.data.network.request;

public class EventsByGpsRequest extends GetEventsRequest {

    public EventsByGpsRequest(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
