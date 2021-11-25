package com.example.drogopolex.events.listeners;

import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.RoutesResponse;

import androidx.lifecycle.LiveData;

public interface RoutesListListener {
    void onRouteDeleteSuccess(LiveData<BasicResponse> responseLiveData, int routeIndex);

    void onGetRoutesSuccess(LiveData<RoutesResponse> responseLiveData);
}
