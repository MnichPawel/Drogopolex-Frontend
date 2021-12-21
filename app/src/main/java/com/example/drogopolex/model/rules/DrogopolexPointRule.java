package com.example.drogopolex.model.rules;

import com.google.android.gms.maps.model.LatLng;

public class DrogopolexPointRule extends DrogopolexRule {
    private final LatLng latLng;

    public DrogopolexPointRule(boolean isAvoid, String description, LatLng latLng) {
        super(isAvoid, description);
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
