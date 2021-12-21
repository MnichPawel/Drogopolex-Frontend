package com.example.drogopolex.subscription.listeners;

import com.example.drogopolex.data.network.response.SubscriptionsResponse;

import androidx.lifecycle.LiveData;

public interface SubscriptionListListener {
    void onSuccess(LiveData<SubscriptionsResponse> response);

    void onFailure(String message);
}
