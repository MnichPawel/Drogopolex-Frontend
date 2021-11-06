package com.example.drogopolex.subscription.listeners;

import com.example.drogopolex.data.network.response.BasicResponse;

import androidx.lifecycle.LiveData;

public interface SubscriptionsListener {
    void onSubscriptionsSuccess(LiveData<BasicResponse> response, int indexToDelete);
    void onFailure(String message);
}
