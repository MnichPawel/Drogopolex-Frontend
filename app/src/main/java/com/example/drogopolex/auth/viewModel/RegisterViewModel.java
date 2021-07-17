package com.example.drogopolex.auth.viewModel;

import android.app.Application;

import com.example.drogopolex.auth.listeners.RegisterListener;
import com.example.drogopolex.auth.utils.RegisterAction;
import com.example.drogopolex.data.network.response.RegisterResponse;
import com.example.drogopolex.data.repositories.UserRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class RegisterViewModel extends AndroidViewModel {
    private MutableLiveData<RegisterAction> mAction = new MutableLiveData<>();

    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> repeatPassword = new MutableLiveData<>();

    private UserRepository userRepository;

    public RegisterListener registerListener = null;

    public RegisterViewModel(@NonNull Application application) {
        super(application);

        userRepository = new UserRepository();
    }

    public LiveData<RegisterAction> getAction(){
        return mAction;
    }

    public void onRegisterClicked(){
        String emailValue = email.getValue();
        String passwordValue = password.getValue();
        String repeatPasswordValue = repeatPassword.getValue();
        String usernameValue = username.getValue();

        if(emailValue == null || emailValue.isEmpty() ||
                passwordValue == null || passwordValue.isEmpty() ||
                repeatPasswordValue == null || repeatPasswordValue.isEmpty() ||
                usernameValue == null || usernameValue.isEmpty()) {
            registerListener.onFailure("Nieprawidłowy email, hasło lub nazwa użytkownika.");

        } else if(!passwordValue.equals(repeatPasswordValue)) {
            registerListener.onFailure("Podane hasła różnią się.");

        } else {
            LiveData<RegisterResponse> registerResponse = userRepository.userRegister(emailValue, usernameValue, passwordValue);
            registerListener.onSuccess(registerResponse);
        }
    }

    public void onReturnClicked(){
        mAction.setValue(new RegisterAction(RegisterAction.SHOW_LOGIN_MENU));
    }
}
