package com.example.drogopolex.auth.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.listeners.LoginListener;
import com.example.drogopolex.auth.utils.LoginAction;
import com.example.drogopolex.auth.viewModel.LoginViewModel;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.databinding.ActivityLoginBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class LoginActivity extends AppCompatActivity implements LoginListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setViewModel(new LoginViewModel());
        activityLoginBinding.executePendingBindings();
        activityLoginBinding.getViewModel().loginListener = this;

        activityLoginBinding.getViewModel().getAction().observe(this, new Observer<LoginAction>() {
            @Override
            public void onChanged(LoginAction loginAction) {
                if(loginAction != null){
                    handleAction(loginAction);
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(sp.getBoolean("loggedIn", false)){
            Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
            startActivity(goToLoggedInMenuActivityIntent);
        }
    }

    private void handleAction(LoginAction loginAction) {
        switch (loginAction.getValue()){
            case LoginAction.SHOW_LOGGED_IN:
                Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
                startActivity(goToLoggedInMenuActivityIntent);
                break;
            case LoginAction.SHOW_LOGIN_MENU:
                Intent goToLoginMenuActivityIntent = new Intent(this, LoginMenuActivity.class);
                startActivity(goToLoginMenuActivityIntent);
                break;
        }
    }

    @Override
    public void onSuccess(LiveData<LoginResponse> response) {
        response.observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse result) {
                if(result != null) {
                    if ("true".equals(result.getSuccess())) {
                        String user_id = result.getUserId();
                        String token = result.getToken();

                        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor spEditor = sp.edit();
                        spEditor.putString("token", token);
                        spEditor.putString("user_id", user_id);
                        spEditor.putBoolean("loggedIn", true);
                        spEditor.apply();

                        handleAction(new LoginAction(LoginAction.SHOW_LOGGED_IN));
                    } else {
                        String errorMessage = result.getError();
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
