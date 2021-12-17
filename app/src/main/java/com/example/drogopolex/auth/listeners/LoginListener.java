package com.example.drogopolex.auth.listeners;

import com.example.drogopolex.data.network.response.LoginResponse;
import com.mopub.mobileads.MoPubInterstitial;

import androidx.lifecycle.LiveData;

public interface LoginListener {
    void onSuccess(LiveData<LoginResponse> response);


    void onInterstitialDismissed(MoPubInterstitial interstitial);

    void onFailure(String message);
}
