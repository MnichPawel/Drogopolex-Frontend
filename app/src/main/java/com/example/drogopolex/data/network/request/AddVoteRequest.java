package com.example.drogopolex.data.network.request;

public class AddVoteRequest {
    final String event_id;
    final String type;

    public AddVoteRequest(String event_id, String type) {
        this.event_id = event_id;
        this.type = type;
    }
}