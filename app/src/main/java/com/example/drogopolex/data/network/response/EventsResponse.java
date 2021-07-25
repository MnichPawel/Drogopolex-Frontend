package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventsResponse {
    @SerializedName("events")
    private List<EventsValue> events;

    public List<EventsValue> getEvents() {
        return events;
    }
}
