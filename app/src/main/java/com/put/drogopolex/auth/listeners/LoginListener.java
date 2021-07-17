package com.put.drogopolex.auth.listeners;

import com.put.drogopolex.data.network.response.LoginResponse;

import androidx.lifecycle.LiveData;

public interface LoginListener {
    void onSuccess(LiveData<LoginResponse> response);
    void onFailure(String message);
}
