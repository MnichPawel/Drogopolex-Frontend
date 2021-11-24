package com.example.drogopolex.data.network.request;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateRouteRequest {
    String name;
    String type;
    String fromName;
    List<Double> fromGPS;
    String toName;
    List<Double> toGPS;
    Avoid avoid;

    public GenerateRouteRequest(String type, LatLng fromGPS, LatLng toGPS) {
        this.type = type;
        this.fromGPS = new ArrayList<>(Arrays.asList(fromGPS.longitude, fromGPS.latitude));
        this.toGPS = new ArrayList<>(Arrays.asList(toGPS.longitude, toGPS.latitude));
    }

    public GenerateRouteRequest(String name, String type, LatLng fromGPS, LatLng toGPS) {
        this.name = name;
        this.type = type;
        this.fromGPS = new ArrayList<>(Arrays.asList(fromGPS.longitude, fromGPS.latitude));
        this.toGPS = new ArrayList<>(Arrays.asList(toGPS.longitude, toGPS.latitude));
    }

    public GenerateRouteRequest(String name, String type, String fromName, String toName) {
        this.name = name;
        this.type = type;
        this.fromName = fromName;
        this.toName = toName;
    }

    public GenerateRouteRequest(String name, String type, String fromName, String toName, Avoid avoid) {
        this.name = name;
        this.type = type;
        this.fromName = fromName;
        this.toName = toName;
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
