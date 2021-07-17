package com.put.drogopolex.auth.viewModel;

import com.put.drogopolex.auth.listeners.LoginListener;
import com.put.drogopolex.auth.utils.LoginAction;
import com.put.drogopolex.data.network.response.LoginResponse;
import com.put.drogopolex.data.repositories.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginAction> mAction = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    private UserRepository userRepository;

    public LoginListener loginListener = null;

    public LoginViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<LoginAction> getAction(){
        return mAction;
    }

    public void onLoginClicked(){
        String emailValue = email.getValue();
        String passwordValue = password.getValue();
        if(emailValue == null || emailValue.isEmpty() ||
                passwordValue == null || passwordValue.isEmpty()){
            loginListener.onFailure("Nieprawidłowy email lub hasło.");
        } else {
            LiveData<LoginResponse> loginResponse = userRepository.userLogin(emailValue, passwordValue);
            loginListener.onSuccess(loginResponse);
        }
    }

    public void onReturnClicked(){
        mAction.setValue(new LoginAction(LoginAction.SHOW_LOGIN_MENU));
    }
}
