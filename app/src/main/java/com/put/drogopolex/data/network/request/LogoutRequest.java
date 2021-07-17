package com.put.drogopolex.data.network.request;

public class LogoutRequest {
    final String user_id;
    final String token;

    public LogoutRequest(String userId, String token) {
        this.user_id = userId;
        this.token = token;
    }
}
