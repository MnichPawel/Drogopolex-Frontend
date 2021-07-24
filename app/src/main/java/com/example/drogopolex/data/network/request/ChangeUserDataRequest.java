package com.example.drogopolex.data.network.request;

public class ChangeUserDataRequest {
    final String user_id;
    final String token;
    final String changed_value;
    final String new_value;


    public ChangeUserDataRequest(String user_id, String token, String changed_value, String new_value) {
        this.user_id = user_id;
        this.token = token;
        this.changed_value = changed_value;
        this.new_value = new_value;
    }
}
