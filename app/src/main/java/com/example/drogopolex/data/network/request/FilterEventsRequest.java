package com.example.drogopolex.data.network.request;

public class FilterEventsRequest {
    final String localization;
    final String type;

    public FilterEventsRequest(String localization, String type) {
        this.localization = localization;
        this.type = type;
    }
}
