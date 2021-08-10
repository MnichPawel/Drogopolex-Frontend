package com.example.drogopolex.events.utils;

public class EventsSearchAction {
    public static final int SHOW_LOGGED_IN = 0;
    private final int mAction;

    public EventsSearchAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
