package com.example.drogopolex.data.network.request;

public class SubscriptionEventsRequest {
    final String token;
    final String user_id;

    public SubscriptionEventsRequest(String token, String user_id) {
        this.token = token;
        this.user_id = user_id;
    }
}
