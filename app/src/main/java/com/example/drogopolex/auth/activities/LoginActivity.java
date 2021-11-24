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
import com.example.drogopolex.events.activities.MapActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

public class LoginActivity extends AppCompatActivity implements LoginListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setViewModel(new LoginViewModel());
        activityLoginBinding.executePendingBindings();
        activityLoginBinding.getViewModel().loginListener = this;

        activityLoginBinding.getViewModel().getAction().observe(this, loginAction -> {
            if (loginAction != null) {
                handleAction(loginAction);
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if (sp.getBoolean("loggedIn", false)) {
            Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
            startActivity(goToMapActivityIntent);
        }
    }

    private void handleAction(LoginAction loginAction) {
        switch (loginAction.getValue()) {
            case LoginAction.SHOW_MAP:
                Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
                startActivity(goToMapActivityIntent);
                break;
            case LoginAction.SHOW_LOGIN_MENU:
                Intent goToLoginMenuActivityIntent = new Intent(this, LoginMenuActivity.class);
                startActivity(goToLoginMenuActivityIntent);
                break;
        }
    }

    @Override
    public void onSuccess(LiveData<LoginResponse> response) {
        response.observe(this, result -> {
            if (result != null) {
                if ("true".equals(result.getSuccess())) {
                    String userId = result.getUserId();
                    String token = result.getToken();

                    SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sp.edit();
                    spEditor.putString("token", token);
                    spEditor.putString("user_id", userId);
                    spEditor.putBoolean("loggedIn", true);
                    spEditor.apply();

                    handleAction(new LoginAction(LoginAction.SHOW_MAP));
                } else {
                    String errorMessage = result.getError();
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
