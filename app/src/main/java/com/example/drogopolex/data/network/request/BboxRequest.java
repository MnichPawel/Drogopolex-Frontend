package com.example.drogopolex.data.network.request;

import com.google.android.gms.maps.model.LatLngBounds;

public class BboxRequest {
    double xmin;
    double xmax;
    double ymin;
    double ymax;

    public BboxRequest(LatLngBounds latLngBounds) {
        this.ymin = latLngBounds.southwest.latitude;
        this.ymax = latLngBounds.northeast.latitude;
        this.xmin = latLngBounds.southwest.longitude;
        this.xmax = latLngBounds.northeast.longitude;
    }
}
