package com.example.drogopolex.data.network.request;

public class RegisterRequest {
    final String email;
    final String name;
    final String password;

    public RegisterRequest(String email, String username, String password) {
        this.email = email;
        this.name = username;
        this.password = password;
    }
}
