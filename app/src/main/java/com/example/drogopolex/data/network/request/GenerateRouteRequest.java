package com.example.drogopolex.data.network.request;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GenerateRouteRequest {
    String name;
    LocationRequest from;
    LocationRequest to;
    Avoid avoid;
    List<LocationRequest> via;
    String drivingProfile;

    public GenerateRouteRequest(LatLng fromGPS, LatLng toGPS) {
        this.from = new LocationRequest(fromGPS.latitude, fromGPS.longitude);
        this.to = new LocationRequest(toGPS.latitude, toGPS.longitude);
    }

    public GenerateRouteRequest(String name, LatLng fromGPS, LatLng toGPS) {
        this.name = name;
        this.from = new LocationRequest(fromGPS.latitude, fromGPS.longitude);
        this.to = new LocationRequest(toGPS.latitude, toGPS.longitude);
    }

    public GenerateRouteRequest(String name, String fromName, String toName) {
        this.name = name;
        this.from = new LocationRequest(fromName);
        this.to = new LocationRequest(toName);
    }

    public GenerateRouteRequest(String name, String fromName, String toName, Avoid avoid) {
        this.name = name;
        this.from = new LocationRequest(fromName);
        this.to = new LocationRequest(toName);
        this.avoid = avoid;
    }

    static class Avoid {
        List<PointToAvoid> points;
        List<String> events;

        public Avoid(List<PointToAvoid> points, List<String> events) {
            this.points = points;
            this.events = events;
        }
    }

    static class PointToAvoid {
        String longitude;
        String latitude;
        String radius;

        public PointToAvoid(String longitude, String latitude, String radius) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
        }
    }
}
