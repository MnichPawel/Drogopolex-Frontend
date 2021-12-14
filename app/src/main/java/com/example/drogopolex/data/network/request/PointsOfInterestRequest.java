package com.example.drogopolex.data.network.request;

public class PointsOfInterestRequest extends GetPointsOfInterest {
    public PointsOfInterestRequest(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
