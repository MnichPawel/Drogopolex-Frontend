package com.example.drogopolex.auth.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.utils.RegisterAction;
import com.example.drogopolex.auth.viewModel.RegisterViewModel;
import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.databinding.ActivityRegisterBinding;
import com.example.drogopolex.events.activities.MapActivity;
import com.example.drogopolex.listeners.BasicListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

public class RegisterActivity extends AppCompatActivity implements BasicListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        activityRegisterBinding.setViewModel(new RegisterViewModel(getApplication()));
        activityRegisterBinding.executePendingBindings();
        activityRegisterBinding.getViewModel().basicListener = this;

        activityRegisterBinding.getViewModel().getAction().observe(this, registerAction -> {
            if (registerAction != null) {
                handleAction(registerAction);
            }
        });

        SharedPreferences sp = getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sp.getBoolean("loggedIn", false)) {
            Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
            startActivity(goToMapActivityIntent);
        }
    }

    private void handleAction(RegisterAction registerAction) {
        switch (registerAction.getValue()) {
            case RegisterAction.SHOW_LOGIN:
                Intent goToLoginActivityIntent = new Intent(this, LoginActivity.class);
                startActivity(goToLoginActivityIntent);
                break;
            case RegisterAction.SHOW_LOGIN_MENU:
                Intent goToLoginMenuActivityIntent = new Intent(this, LoginMenuActivity.class);
                startActivity(goToLoginMenuActivityIntent);
                break;
            default:
                Log.e("RegisterActivity", "Unknown action.");
        }
    }

    @Override
    public void onSuccess(LiveData<BasicResponse> response) {
        response.observe(this, result -> {
            if (result != null) {
                if(result.getError() == null) {
                    Toast.makeText(RegisterActivity.this, "Konto utworzone", Toast.LENGTH_LONG).show();
                    handleAction(new RegisterAction(RegisterAction.SHOW_LOGIN));
                } else {
                    Toast.makeText(RegisterActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Nie uda??o si?? przetworzy?? odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}