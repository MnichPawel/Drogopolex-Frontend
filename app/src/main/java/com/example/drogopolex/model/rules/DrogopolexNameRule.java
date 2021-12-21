package com.example.drogopolex.model.rules;

public class DrogopolexNameRule extends DrogopolexRule {
    private final String placeName;

    public DrogopolexNameRule(boolean isAvoid, String description, String placeName) {
        super(isAvoid, description);
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return placeName;
    }
}
