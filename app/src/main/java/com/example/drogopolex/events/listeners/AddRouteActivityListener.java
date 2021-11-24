package com.example.drogopolex.events.listeners;

import com.example.drogopolex.data.network.response.RouteResponse;

import androidx.lifecycle.LiveData;

public interface AddRouteActivityListener {
    void onSuccessAddRoute(LiveData<RouteResponse> routeResponseLiveData);
}
