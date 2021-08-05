package com.example.drogopolex.data.network.request;

public class AddEventRequest {
    final boolean gps;
    final String localization;
    final String latitude;
    final String longitude;
    final String type;
    final String user_id;
    final String token;

    public AddEventRequest(boolean gps, String localization, String latitude, String longitude, String type, String user_id, String token) {
        this.gps = gps;
        this.localization = localization;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.user_id = user_id;
        this.token = token;
    }
}
