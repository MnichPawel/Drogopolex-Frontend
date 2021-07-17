package com.put.drogopolex.auth.viewModel;

import android.content.SharedPreferences;

import com.put.drogopolex.auth.listeners.BasicListener;
import com.put.drogopolex.auth.listeners.SharedPreferencesHolder;
import com.put.drogopolex.auth.utils.LoggedInMenuAction;
import com.put.drogopolex.data.network.response.BasicResponse;
import com.put.drogopolex.data.repositories.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoggedInMenuViewModel extends ViewModel {
    private MutableLiveData<LoggedInMenuAction> mAction = new MutableLiveData<>();

    private UserRepository userRepository;
    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public BasicListener logoutListener = null;

    public LoggedInMenuViewModel() {
        this.userRepository = new UserRepository();
    }

    public LiveData<LoggedInMenuAction> getAction(){
        return mAction;
    }

    public void onNewEventClicked(){
        mAction.setValue(new LoggedInMenuAction(LoggedInMenuAction.SHOW_NEW_EVENT));
    }

    public void onEventsClicked(){
        mAction.setValue(new LoggedInMenuAction(LoggedInMenuAction.SHOW_EVENTS));
    }

    public void onSubscriptionsClicked(){
        mAction.setValue(new LoggedInMenuAction(LoggedInMenuAction.SHOW_SUBSCRIPTIONS));
    }

    public void onProfileClicked(){
        mAction.setValue(new LoggedInMenuAction(LoggedInMenuAction.SHOW_PROFILE));
    }

    public void onLogoutClicked(){
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        boolean loggedIn = sharedPreferences.getBoolean("loggedIn", false);
        String user_id = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");

        if(!loggedIn){
            logoutListener.onFailure("Użytkownik nie jest zalogowany");
        }

        LiveData<BasicResponse> response = userRepository.userLogout(user_id, token);
        logoutListener.onSuccess(response);
    }
}
