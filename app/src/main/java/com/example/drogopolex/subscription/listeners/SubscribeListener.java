package com.example.drogopolex.subscription.listeners;

import com.example.drogopolex.data.network.response.BasicResponse;


import androidx.lifecycle.LiveData;

public interface SubscribeListener {
    void onSuccess(LiveData<BasicResponse> response);
    void onFailure(String message);
}
