package com.example.drogopolex.auth.utils;

public enum UserDataType {
    USERNAME("username"),
    PASSWORD("password"),
    EMAIL("email");

    String type;

    UserDataType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
