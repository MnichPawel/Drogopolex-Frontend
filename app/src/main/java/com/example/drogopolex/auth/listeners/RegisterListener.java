package com.example.drogopolex.auth.listeners;

import com.example.drogopolex.data.network.response.RegisterResponse;

import androidx.lifecycle.LiveData;

public interface RegisterListener {
    void onSuccess(LiveData<RegisterResponse> response);
    void onFailure(String message);
}
