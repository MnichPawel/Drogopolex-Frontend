package com.example.drogopolex.auth.viewModel;

import android.content.SharedPreferences;

import com.example.drogopolex.auth.utils.ProfileAction;
import com.example.drogopolex.auth.utils.UserDataType;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.ProfileResponse;
import com.example.drogopolex.data.repositories.UserRepository;
import com.example.drogopolex.listeners.BasicListener;
import com.example.drogopolex.listeners.SharedPreferencesHolder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<ProfileAction> mAction = new MutableLiveData<>();
    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> repeatPassword = new MutableLiveData<>();

    private UserRepository userRepository;

    public BasicListener basicListener = null;
    public SharedPreferencesHolder sharedPreferencesHolder = null;

    public ProfileViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<ProfileAction> getAction(){
        return mAction;
    }

    public LiveData<ProfileResponse> getUserData() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String user_id = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");

        return userRepository.userGetUserData(user_id, token);
    }

    public void onReturnClicked() {
        mAction.setValue(new ProfileAction(ProfileAction.SHOW_MAP));
    }

    public void onChangeUsernameClicked() {
        String usernameValue = username.getValue();
        if(usernameValue == null || usernameValue.isEmpty()){
            basicListener.onFailure("Niepoprawna nazwa użytkownika.");
        } else {
            SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
            String user_id = sharedPreferences.getString("user_id", "");
            String token = sharedPreferences.getString("token", "");
            LiveData<BasicResponse> response = userRepository.userChangeUserData(user_id, token, UserDataType.USERNAME.getType(), usernameValue);
            basicListener.onSuccess(response);
        }
    }

    public void onChangePasswordClicked() {
        String passwordValue = password.getValue();
        String repeatPasswordValue = repeatPassword.getValue();

        if(passwordValue == null || passwordValue.isEmpty() ||
        repeatPasswordValue == null || repeatPasswordValue.isEmpty()){
            basicListener.onFailure("Niepoprawne hasło.");
        }
        else if(!passwordValue.equals(repeatPasswordValue)){
            basicListener.onFailure("Podane hasła różnią się.");
        } else {
            SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
            String user_id = sharedPreferences.getString("user_id", "");
            String token = sharedPreferences.getString("token", "");
            LiveData<BasicResponse> response = userRepository.userChangeUserData(user_id, token, UserDataType.PASSWORD.getType(), passwordValue);
            basicListener.onSuccess(response);
        }
    }

    public void onChangeEmailClicked() {
        String emailValue = email.getValue();
        if(emailValue == null || emailValue.isEmpty()){
            basicListener.onFailure("Niepoprawny adres email.");
        } else {
            SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
            String user_id = sharedPreferences.getString("user_id", "");
            String token = sharedPreferences.getString("token", "");
            LiveData<BasicResponse> response = userRepository.userChangeUserData(user_id, token, UserDataType.EMAIL.getType(), emailValue);
            basicListener.onSuccess(response);
        }
    }
}
