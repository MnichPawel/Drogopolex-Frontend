package com.example.drogopolex.events.utils;

public class RoutesListAction {
    public static final int SHOW_MAP = 0;
    public static final int SHOW_ADD_ROUTE = 1;
    private final int mAction;

    public RoutesListAction(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
