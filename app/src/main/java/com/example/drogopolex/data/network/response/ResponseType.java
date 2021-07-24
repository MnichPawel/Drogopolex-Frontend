package com.example.drogopolex.data.network.response;

public enum ResponseType {
    LOGIN_RESPONSE(0),
    BASIC_RESPONSE(1),
    PROFILE_RESPONSE(2);

    int id;

    ResponseType(int typeId) {
        this.id = typeId;
    }
}
