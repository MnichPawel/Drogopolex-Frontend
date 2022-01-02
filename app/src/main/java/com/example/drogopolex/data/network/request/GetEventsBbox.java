package com.example.drogopolex.data.network.request;

import com.google.android.gms.maps.model.LatLngBounds;

public class GetEventsBbox extends GetEventsRequest{
    public GetEventsBbox(LatLngBounds latLngBounds) {
        this.bbox = new BboxRequest(latLngBounds);
    }
}
