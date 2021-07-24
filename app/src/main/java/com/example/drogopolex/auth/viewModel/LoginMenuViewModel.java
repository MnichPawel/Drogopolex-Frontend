package com.example.drogopolex.auth.viewModel;

import com.example.drogopolex.auth.utils.LoginMenuAction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginMenuViewModel extends ViewModel {

    private MutableLiveData<LoginMenuAction> mAction = new MutableLiveData<>();

    public LiveData<LoginMenuAction> getAction(){
        return mAction;
    }

    public void onLoginClicked(){
        mAction.setValue(new LoginMenuAction(LoginMenuAction.SHOW_LOGIN));
    }

    public void onRegisterClicked(){
        mAction.setValue(new LoginMenuAction(LoginMenuAction.SHOW_REGISTER));
    }
}
