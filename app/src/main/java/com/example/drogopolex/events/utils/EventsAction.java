package com.example.drogopolex.events.utils;

public class EventsAction {
    public static final int SHOW_LOGGED_IN = 0;
    public static final int SHOW_EVENTS_SEARCH = 1;
    private final int mAction;

    public EventsAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
