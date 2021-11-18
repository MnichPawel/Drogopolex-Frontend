package com.example.drogopolex.data.network.request;

public class FilterEventsRequest extends GetEventsRequest {

    public FilterEventsRequest(String localization, String type) {
        this.localization = localization;
        this.type = type;
    }
}
