package com.put.drogopolex.model;

import java.util.List;

public class DrogopolexEvent {
    private String type;
    private String location;
    private String street;
    private int id;
    private VoteType userVoteType;
    private List<Vote> votes;

    public DrogopolexEvent(String type, String location, String street, int id, List<Vote> votes, VoteType userVoteType) {
        this.type = type;
        this.location = location;
        this.street = street;
        this.id = id;
        this.votes = votes;
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

    public int getVotesCount() {
        int upvotes = (int) votes.stream()
                .filter(vote -> vote.getType() == VoteType.UPVOTED)
                .count();
        int downvotes = (int) votes.stream()
                .filter(vote -> vote.getType() == VoteType.DOWNVOTED)
                .count();
        return upvotes - downvotes;
    }
}
