package com.example.drogopolex.events.listeners;

import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.model.rules.DrogopolexRule;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface AddRouteActivityListener {
    void onSuccessAddRoute(LiveData<RouteValue> routeResponseLiveData);

    LatLng getLatLngOfChosenPoint(boolean isSource);

    List<DrogopolexRule> getRules();
}
