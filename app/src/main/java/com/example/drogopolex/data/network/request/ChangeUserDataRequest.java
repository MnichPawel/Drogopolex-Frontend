package com.example.drogopolex.data.network.request;

public class ChangeUserDataRequest {
    final String changed_value;
    final String new_value;


    public ChangeUserDataRequest(String changed_value, String new_value) {
        this.changed_value = changed_value;
        this.new_value = new_value;
    }
}
