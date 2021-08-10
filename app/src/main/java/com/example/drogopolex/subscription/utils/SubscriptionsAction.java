package com.example.drogopolex.subscription.utils;

public class SubscriptionsAction {
    public static final int SHOW_LOGGED_IN = 0;
    public static final int SHOW_SUBSCRIBED_EVENTS=1;
    private final int mAction;

    public SubscriptionsAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
