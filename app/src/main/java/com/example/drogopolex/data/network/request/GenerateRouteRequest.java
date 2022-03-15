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

    public GenerateRouteRequest(LatLng fromGPS, LatLng toGPS, Avoid avoid, List<LocationRequest> via) {
        this.from = new LocationRequest(fromGPS.latitude, fromGPS.longitude);
        this.to = new LocationRequest(toGPS.latitude, toGPS.longitude);
        this.avoid = avoid;
        this.via = via;
    }

    public GenerateRouteRequest(String name, LatLng fromGPS, LatLng toGPS, Avoid avoid, List<LocationRequest> via) {
        this.name = name;
        this.from = new LocationRequest(fromGPS.latitude, fromGPS.longitude);
        this.to = new LocationRequest(toGPS.latitude, toGPS.longitude);
        this.avoid = avoid;
        this.via = via;
    }

    public GenerateRouteRequest(String name, String fromName, String toName, Avoid avoid, List<LocationRequest> via) {
        this.name = name;
        this.from = new LocationRequest(fromName);
        this.to = new LocationRequest(toName);
        this.avoid = avoid;
        this.via = via;
    }

    public GenerateRouteRequest(String name, LatLng fromGps, String toName, Avoid avoid, List<LocationRequest> via) {
        this.name = name;
        this.from = new LocationRequest(fromGps.latitude, fromGps.longitude);
        this.to = new LocationRequest(toName);
        this.avoid = avoid;
        this.via = via;
    }

    public GenerateRouteRequest(String name, String fromName, LatLng toGps, Avoid avoid, List<LocationRequest> via) {
        this.name = name;
        this.from = new LocationRequest(fromName);
        this.to = new LocationRequest(toGps.latitude, toGps.longitude);
        this.avoid = avoid;
        this.via = via;
    }

    public static class Avoid {
        List<PointToAvoid> points;
        List<String> events;

        public Avoid(List<PointToAvoid> points, List<String> events) {
            this.points = points;
            this.events = events;
        }
    }

    public static class PointToAvoid {
        double longitude;
        double latitude;
        int radius;

        public PointToAvoid(double latitude, double longitude, int radius) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
        }
    }
}
