package com.example.drogopolex.subscription.viewModel;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.drogopolex.auth.listeners.LoginListener;
import com.example.drogopolex.auth.listeners.SharedPreferencesHolder;
import com.example.drogopolex.auth.utils.LoginAction;
import com.example.drogopolex.auth.utils.ProfileAction;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.repositories.UserRepository;
import com.example.drogopolex.subscription.activities.SubscribeActivity;
import com.example.drogopolex.subscription.listeners.SubscribeListener;
import com.example.drogopolex.subscription.utils.SubscribeAction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubscribeViewModel extends ViewModel {
    private MutableLiveData<SubscribeAction> mAction = new MutableLiveData<>();
    public MutableLiveData<String> localization = new MutableLiveData<>();
    public SharedPreferencesHolder sharedPreferencesHolder = null;
    private UserRepository userRepository;

    public SubscribeListener subscribeListener = null;

    public SubscribeViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<SubscribeAction> getAction(){
        return mAction;
    }

    public void onSubscribeClicked(){

        String localizationValue = localization.getValue();

        String coordinates = "(200, 300)"; //todo hardcoded value
        SharedPreferences sp = sharedPreferencesHolder.getSharedPreferences();
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        //if(passwordValue == null || passwordValue.isEmpty()){
        //    loginListener.onFailure("Nieprawidłowa lokalizacja");
        //} else {

            LiveData<BasicResponse> subscribeResponse = userRepository.userSubscribe(localizationValue,coordinates,user_id,token);
        subscribeListener.onSuccess(subscribeResponse);
        //}
    }

    public void onReturnClicked(){
        mAction.setValue(new SubscribeAction(SubscribeAction.SHOW_SUBSCRIBED));
    }
}
