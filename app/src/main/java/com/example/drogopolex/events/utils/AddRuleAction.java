package com.example.drogopolex.events.utils;

public enum AddRuleAction {
    AVOID_BY_POINT("Omijaj (punkt)"),
    AVOID_EVENT_TYPE("Omijaj (typ zdarzenia)"),
    NAVIGATE_THROUGH_BY_NAME("Prowadź przez (nazwa)"),
    NAVIGATE_THROUGH_BY_POINT("Prowadź przez (punkt)");

    String mAction;

    AddRuleAction(String action) {
        this.mAction = action;
    }

    public String getValue() {
        return mAction;
    }
}
