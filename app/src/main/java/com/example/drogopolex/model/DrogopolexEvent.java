package com.example.drogopolex.model;

import com.google.android.gms.maps.model.LatLng;

public class DrogopolexEvent {
    private String type;
    private String location;
    private String street;
    private int id;
    private VoteType userVoteType;
    private int valueOfVotes;
    private LatLng coordinates;

    public DrogopolexEvent(String type, String location, String street, int id, LatLng coordinates, int valueOfVotes, VoteType userVoteType) {
        this.type = type;
        this.location = location;
        this.street = street;
        this.id = id;
        this.coordinates = coordinates;
        this.valueOfVotes = valueOfVotes;
        this.userVoteType = userVoteType;
    }

    public String getType(){
        return type;
    }

    public String getLocation(){
        return location;
    }

    public String getStreet() {
        return street;
    }

    public int getId(){
        return id;
    }

    public VoteType getUserVoteType() {
        return userVoteType;
    }

    public void setUserVoteType(VoteType userVoteType) {
        this.userVoteType = userVoteType;
    }

    public int getValueOfVotes() {
        return valueOfVotes;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }
}
