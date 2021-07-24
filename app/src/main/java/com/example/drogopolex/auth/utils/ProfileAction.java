package com.example.drogopolex.auth.utils;

public class ProfileAction {
    public static final int SHOW_LOGGED_IN = 0;
    private final int mAction;

    public ProfileAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
