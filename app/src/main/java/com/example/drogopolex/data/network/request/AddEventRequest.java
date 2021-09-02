package com.example.drogopolex.data.network.request;

public class AddEventRequest {
    final boolean gps;
    final String localization;
    final String latitude;
    final String longitude;
    final String type;

    public AddEventRequest(boolean gps, String localization, String latitude, String longitude, String type) {
        this.gps = gps;
        this.localization = localization;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }
}
