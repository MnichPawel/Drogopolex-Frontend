package com.example.drogopolex.auth.utils;

public class LoggedInMenuAction {
    public static final int SHOW_NEW_EVENT= 0;
    public static final int SHOW_EVENTS = 1;
    public static final int SHOW_SUBSCRIPTIONS = 2;
    public static final int SHOW_PROFILE = 3;
    public static final int SHOW_LOGIN_MENU = 4;
    public static final int SHOW_MAP = 5;
    private final int mAction;

    public LoggedInMenuAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
