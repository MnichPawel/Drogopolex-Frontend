package com.example.drogopolex.events.listeners;

import com.example.drogopolex.data.network.response.RouteValue;

import androidx.lifecycle.LiveData;

public interface AddRouteActivityListener {
    void onSuccessAddRoute(LiveData<RouteValue> routeResponseLiveData);
}
