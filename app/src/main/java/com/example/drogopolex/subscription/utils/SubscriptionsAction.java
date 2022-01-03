package com.example.drogopolex.subscription.utils;

public class SubscriptionsAction {
    public static final int SHOW_MAP = 0;
    public static final int SHOW_SUBSCRIBE = 1;
    public static final int SHOW_SUBSCRIBED_EVENTS = 2;
    private final int mAction;

    public SubscriptionsAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
