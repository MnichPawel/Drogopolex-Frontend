package com.example.drogopolex.subscription.viewModel;

import android.content.SharedPreferences;

import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.subscription.listeners.SubscribeListener;
import com.example.drogopolex.subscription.utils.SubscribeAction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubscribeViewModel extends ViewModel {
    private MutableLiveData<SubscribeAction> mAction = new MutableLiveData<>();

    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public SubscribeListener subscribeListener = null;
    private SubscriptionsRepository subscriptionsRepository;

    public MutableLiveData<String> localization = new MutableLiveData<>();


    public SubscribeViewModel() {
        subscriptionsRepository = new SubscriptionsRepository();
    }

    public LiveData<SubscribeAction> getAction(){
        return mAction;
    }

    public void onSubscribeClicked(){
        String coordinates = "(200, 300)"; //todo hardcoded value //probably useless data, will be deleted in new backend version

        SharedPreferences sp = sharedPreferencesHolder.getSharedPreferences();
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        String localizationValue = localization.getValue();
        if(localizationValue == null || localizationValue.isEmpty()){
            subscribeListener.onFailure("Nieprawidłowa lokalizacja.");
        } else {
            LiveData<BasicResponse> subscribeResponse = subscriptionsRepository.subscriptionSubscribe(localizationValue, coordinates, user_id, token);
            subscribeListener.onSuccess(subscribeResponse);
        }
    }

    public void onReturnClicked(){
        mAction.setValue(new SubscribeAction(SubscribeAction.SHOW_SUBSCRIPTIONS));
    }
}
