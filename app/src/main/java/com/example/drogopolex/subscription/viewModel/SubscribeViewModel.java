package com.example.drogopolex.subscription.viewModel;

import android.content.SharedPreferences;

import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.subscription.listeners.SubscribeListener;
import com.example.drogopolex.subscription.utils.SubscribeAction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubscribeViewModel extends ViewModel {
    private final MutableLiveData<SubscribeAction> mAction = new MutableLiveData<>();
    private final SubscriptionsRepository subscriptionsRepository;
    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public SubscribeListener subscribeListener = null;
    public MutableLiveData<String> localization = new MutableLiveData<>();


    public SubscribeViewModel() {
        subscriptionsRepository = new SubscriptionsRepository();
    }

    public LiveData<SubscribeAction> getAction() {
        return mAction;
    }

    public void onSubscribeClicked() {
        SharedPreferences sp = sharedPreferencesHolder.getSharedPreferences();
        String userId = sp.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sp.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        String localizationValue = localization.getValue();
        if (localizationValue == null || localizationValue.isEmpty()) {
            subscribeListener.onFailure("Nieprawidłowa lokalizacja.");
        } else {
            LiveData<BasicResponse> subscribeResponse = subscriptionsRepository.subscriptionSubscribe(localizationValue, userId, token);
            subscribeListener.onSuccess(subscribeResponse);
        }
    }

    public void onReturnClicked() {
        mAction.setValue(new SubscribeAction(SubscribeAction.SHOW_SUBSCRIPTIONS));
    }
}
