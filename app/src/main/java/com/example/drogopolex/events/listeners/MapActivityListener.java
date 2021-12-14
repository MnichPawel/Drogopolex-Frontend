package com.example.drogopolex.events.listeners;

import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.PointsOfInterestResponse;
import com.example.drogopolex.data.network.response.RouteValue;
import com.google.android.gms.maps.model.LatLng;

import androidx.lifecycle.LiveData;

public interface MapActivityListener {
    void onAddNewEventSuccess(LiveData<BasicResponse> response);

    void onGetEventsSuccess(LiveData<EventsResponse> eventsResponseLiveData);

    void onLogoutSuccess(LiveData<BasicResponse> responseLiveData);

    void onLogoutFailure(String message);

    void onChoosePointModeEntered(LatLng location);

    LatLng getChosenPoint();

    void drawRoute(LiveData<RouteValue> routeResponseLiveData);

    void onGetPOISuccess(LiveData<PointsOfInterestResponse> pointsOfInterestResponseLiveData);
}
