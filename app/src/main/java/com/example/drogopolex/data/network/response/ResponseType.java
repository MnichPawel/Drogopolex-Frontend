package com.example.drogopolex.data.network.response;

public enum ResponseType {
    LOGIN_RESPONSE(0),
    REGISTER_RESPONSE(1);

    int id;

    ResponseType(int typeId) {
        this.id = typeId;
    }
}
