package com.example.drogopolex.model.rules;

public abstract class DrogopolexRule {
    private final boolean isAvoid;
    private final String description;

    public DrogopolexRule(boolean isAvoid, String description) {
        this.isAvoid = isAvoid;
        this.description = description;
    }

    public boolean isAvoid() {
        return isAvoid;
    }

    public String getDescription() {
        return description;
    }
}
