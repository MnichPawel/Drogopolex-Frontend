package com.example.drogopolex.events.utils;

public class NewEventAction {
    public static final int SHOW_LOGGED_IN = 0;
    public static final int ADD_EVENT_BY_GPS = 1;
    private final int mAction;

    public NewEventAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
