package com.example.drogopolex.data.network.request;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateRouteRequest {
    String name;
    String type;
    String fromName;
    List<Double> from;
    String toName;
    List<Double> to;
    Avoid avoid;

    public GenerateRouteRequest(String type, LatLng from, LatLng to) {
        this.type = type;
        this.from = new ArrayList<>(Arrays.asList(from.longitude, from.latitude));
        this.to = new ArrayList<>(Arrays.asList(to.longitude, to.latitude));
    }

    public GenerateRouteRequest(String name, String type, LatLng from, LatLng to) {
        this.name = name;
        this.type = type;
        this.from = new ArrayList<>(Arrays.asList(from.longitude, from.latitude));
        this.to = new ArrayList<>(Arrays.asList(to.longitude, to.latitude));
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
