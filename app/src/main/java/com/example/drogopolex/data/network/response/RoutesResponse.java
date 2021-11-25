package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoutesResponse {
    @SerializedName("routes")
    private List<RouteValue> routes;

    public List<RouteValue> getRoutes() {
        return routes;
    }
}
