package com.example.drogopolex.model.rules;

public class DrogopolexEventTypeRule extends DrogopolexRule{
    private String eventType;

    public DrogopolexEventTypeRule(String description, String eventType) {
        super(true, description);
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
}
