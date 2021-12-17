package com.example.drogopolex.model;

public class DrogopolexRoute {
    private String routeId;
    private String name;
    private String sourceName;
    private String destinationName;
    private Double time;
    private Double distance;


    public DrogopolexRoute(String routeId, String name, String sourceName, String destinationName, Double time, Double distance) {
        this.routeId = routeId;
        this.name = name;
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.time = time;
        this.distance = distance;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getName() {
        return name;
    }

    public Double getTime() {
        return time;
    }

    public Double getDistance() {
        return distance;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }
}
