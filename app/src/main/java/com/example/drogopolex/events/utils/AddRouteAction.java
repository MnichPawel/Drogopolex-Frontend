package com.example.drogopolex.events.utils;

public class AddRouteAction {
    public static final int SHOW_MAP = 0;
    private final int mAction;

    public AddRouteAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
