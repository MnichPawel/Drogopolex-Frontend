package com.example.drogopolex.data.network.request;

public class SubscriptionsRequest {
    final String token;
    final String user_id;

    public SubscriptionsRequest(String token, String user_id) {
        this.token = token;
        this.user_id = user_id;
    }
}
