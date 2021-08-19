package com.example.drogopolex.data.network.request;

public class ChangeVoteRequest {
    final String event_id;
    final String new_type;

    public ChangeVoteRequest(String event_id, String type) {
        this.event_id = event_id;
        this.new_type = type;
    }
}
