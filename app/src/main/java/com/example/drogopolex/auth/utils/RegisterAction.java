package com.example.drogopolex.auth.utils;

public class RegisterAction {
    public static final int SHOW_LOGIN = 0;
    public static final int SHOW_LOGIN_MENU = 1;
    private final int mAction;

    public RegisterAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
