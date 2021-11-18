package com.example.drogopolex.events.utils;

public class MapAction {
    public static final int SHOW_SUBSCRIPTIONS = 0;
    public static final int SHOW_PROFILE = 1;
    public static final int LOGOUT = 2;

    private final int mAction;

    public MapAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
