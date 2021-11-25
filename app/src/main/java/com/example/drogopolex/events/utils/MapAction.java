package com.example.drogopolex.events.utils;

public class MapAction {
    public static final int SHOW_SUBSCRIPTIONS = 0;
    public static final int SHOW_PROFILE = 1;
    public static final int SHOW_ROUTES_LIST = 2;
    public static final int LOGOUT = 3;
    public static final int RESET_ROUTE = 4;

    private final int mAction;

    public MapAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
