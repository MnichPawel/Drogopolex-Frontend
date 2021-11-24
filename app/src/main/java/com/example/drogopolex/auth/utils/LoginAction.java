package com.example.drogopolex.auth.utils;

public class LoginAction {
    public static final int SHOW_MAP = 0;
    public static final int SHOW_LOGIN_MENU = 1;
    private final int mAction;

    public LoginAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
