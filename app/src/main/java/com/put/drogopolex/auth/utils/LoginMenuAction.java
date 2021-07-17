package com.put.drogopolex.auth.utils;

public class LoginMenuAction {
    public static final int SHOW_LOGIN= 0;
    public static final int SHOW_REGISTER = 1;
    private final int mAction;

    public LoginMenuAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
