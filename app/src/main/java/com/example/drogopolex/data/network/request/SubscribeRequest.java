package com.example.drogopolex.data.network.request;

public class SubscribeRequest {
    final String localization;
    final String coordinates;

    public SubscribeRequest(String localization, String coordinates) {
        this.localization = localization;
        this.coordinates = coordinates;
    }
}
