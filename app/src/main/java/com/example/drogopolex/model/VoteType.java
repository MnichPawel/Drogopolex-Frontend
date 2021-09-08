package com.example.drogopolex.model;

public enum VoteType {
    UPVOTED("T"),
    DOWNVOTED("F"),
    NO_VOTE("N");

    String value;

    VoteType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}