package com.example.drogopolex.events.listeners;

import com.example.drogopolex.data.network.response.EventsResponse;

import androidx.lifecycle.LiveData;

public interface EventsListener {
    void onSuccess(LiveData<EventsResponse> response);
    void onFailure(String message);
}
