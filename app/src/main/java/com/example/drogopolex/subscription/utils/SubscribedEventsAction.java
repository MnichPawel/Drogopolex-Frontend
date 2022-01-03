package com.example.drogopolex.subscription.utils;

public class SubscribedEventsAction {
    public static final int ADD_SUBSCRIPTION = 0;
    public static final int SHOW_SUBSCRIPTIONS = 1;
    private final int mAction;

    public SubscribedEventsAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}