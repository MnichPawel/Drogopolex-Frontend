package com.example.drogopolex.events.utils;

public class AddRouteAction {
    public static final int SHOW_ROUTES_LIST = 0;
    public static final int SHOW_POPUP = 1;
    public static final int CLOSE_POPUP = 2;
    private final int mAction;

    public AddRouteAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
