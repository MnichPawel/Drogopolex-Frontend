package com.example.drogopolex.data.network.request;

public class BasicRequest {
    final String user_id;
    final String token;

    public BasicRequest(String userId, String token) {
        this.user_id = userId;
        this.token = token;
    }
}
