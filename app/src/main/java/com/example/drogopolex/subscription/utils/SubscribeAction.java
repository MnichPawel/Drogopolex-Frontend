package com.example.drogopolex.subscription.utils;

public class SubscribeAction {
    public static final int SHOW_SUBSCRIPTIONS = 0;
    private final int mAction;
    public SubscribeAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
