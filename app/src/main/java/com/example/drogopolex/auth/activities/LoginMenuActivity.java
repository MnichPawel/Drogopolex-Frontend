package com.example.drogopolex.auth.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.utils.LoginMenuAction;
import com.example.drogopolex.auth.viewModel.LoginMenuViewModel;
import com.example.drogopolex.databinding.ActivityLoginMenuBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

public class LoginMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginMenuBinding activityLoginMenuBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_menu);
        activityLoginMenuBinding.setViewModel(new LoginMenuViewModel());
        activityLoginMenuBinding.executePendingBindings();

        activityLoginMenuBinding.getViewModel().getAction().observe(this, new Observer<LoginMenuAction>() {
            @Override
            public void onChanged(LoginMenuAction loginMenuAction) {
                if(loginMenuAction != null){
                    handleAction(loginMenuAction);
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(sp.getBoolean("loggedIn", false)){
            Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
            startActivity(goToLoggedInMenuActivityIntent);
        }
    }

    private void handleAction(LoginMenuAction loginMenuAction) {
        switch (loginMenuAction.getValue()){
            case LoginMenuAction.SHOW_LOGIN:
                Intent goToLoginActivityIntent = new Intent(this, LoginActivity.class);
                startActivity(goToLoginActivityIntent);
                break;
            case LoginMenuAction.SHOW_REGISTER:
                Intent goToRegisterActivityIntent = new Intent(this, RegisterActivity.class);
                startActivity(goToRegisterActivityIntent);
                break;
        }
    }
}