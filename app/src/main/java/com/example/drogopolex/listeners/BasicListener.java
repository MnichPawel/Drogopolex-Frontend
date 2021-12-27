package com.example.drogopolex.listeners;

import com.example.drogopolex.data.network.response.BasicResponse;

import androidx.lifecycle.LiveData;

public interface BasicListener {
    void onSuccess(LiveData<BasicResponse> response);

    void onFailure(String message);
}
