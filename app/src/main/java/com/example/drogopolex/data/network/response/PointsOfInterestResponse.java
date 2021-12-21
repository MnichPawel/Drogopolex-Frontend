package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PointsOfInterestResponse {
    @SerializedName("places")
    private List<PointsOfInterestValue> pointsOfInterest;

    public List<PointsOfInterestValue> getPois() {
        return pointsOfInterest;
    }
}
