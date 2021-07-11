package com.example.drogopolex.auth.listeners;

import com.example.drogopolex.data.network.response.LoginResponse;

import androidx.lifecycle.LiveData;

public interface AuthListener {
    void onSuccess(LiveData<LoginResponse> response);
    void onFailure(String message);
}
