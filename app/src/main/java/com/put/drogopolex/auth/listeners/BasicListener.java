package com.put.drogopolex.auth.listeners;

import com.put.drogopolex.data.network.response.BasicResponse;

import androidx.lifecycle.LiveData;

public interface BasicListener {
    void onSuccess(LiveData<BasicResponse> response);
    void onFailure(String message);
}
