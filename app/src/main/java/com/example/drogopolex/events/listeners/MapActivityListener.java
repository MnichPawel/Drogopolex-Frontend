package com.example.drogopolex.events.listeners;

import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;

import androidx.lifecycle.LiveData;

public interface MapActivityListener {
    void onAddNewEventSuccess(LiveData<BasicResponse> response);

    void onGetEventsSuccess(LiveData<EventsResponse> eventsResponseLiveData);

    void onLogoutSuccess(LiveData<BasicResponse> responseLiveData);

    void onLogoutFailure(String message);
}
