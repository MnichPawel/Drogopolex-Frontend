package com.example.drogopolex.events.utils;

public class AddRouteAction {
    public static final int SHOW_ROUTES_LIST = 0;
    public static final int CHOOSE_SOURCE_POINT = 1;
    public static final int CHOOSE_DESTINATION_POINT = 2;
    public static final int ACCEPT_POPUP = 3;
    public static final int CANCEL_POPUP = 4;
    public static final int SHOW_ADD_RULE_POPUP = 5;
    private final int mAction;

    public AddRouteAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
