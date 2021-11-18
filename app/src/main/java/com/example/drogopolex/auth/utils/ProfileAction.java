package com.example.drogopolex.auth.utils;

public class ProfileAction {
    public static final int SHOW_MAP = 0;
    private final int mAction;

    public ProfileAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
