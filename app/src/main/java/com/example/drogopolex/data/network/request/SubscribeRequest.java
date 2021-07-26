package com.example.drogopolex.data.network.request;

public class SubscribeRequest {
    final String localization;
    final String coordinates;
    final String user_id;
    final String token;

    public SubscribeRequest(String localization, String coordinates,String user_id, String token) {
        this.localization = localization;
        this.coordinates = coordinates;
        this.user_id=user_id;
        this.token=token;
    }
}
