package com.example.drogopolex.model;

public class Vote {
    private final int eventId;
    private final int userId;
    private final VoteType type;

    public Vote(int eventId, int userId, String type) {
        this.eventId = eventId;
        this.userId = userId;
        if ("T".equals(type)) this.type = VoteType.UPVOTED;
        else this.type = VoteType.DOWNVOTED;
    }

    public int getEventId() {
        return eventId;
    }

    public int getUserId() {
        return userId;
    }

    public VoteType getType() {
        return type;
    }
}
