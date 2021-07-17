package com.example.drogopolex.auth.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.listeners.BasicListener;
import com.example.drogopolex.auth.utils.RegisterAction;
import com.example.drogopolex.auth.viewModel.RegisterViewModel;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.databinding.ActivityRegisterBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class RegisterActivity extends AppCompatActivity implements BasicListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        activityMainBinding.setViewModel(new RegisterViewModel(getApplication()));
        activityMainBinding.executePendingBindings();
        activityMainBinding.getViewModel().basicListener = this;

        activityMainBinding.getViewModel().getAction().observe(this, new Observer<RegisterAction>() {
            @Override
            public void onChanged(RegisterAction registerAction) {
                if(registerAction != null){
                    handleAction(registerAction);
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(sp.getBoolean("loggedIn", false)){
            Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
            startActivity(goToLoggedInMenuActivityIntent);
        }
    }

    private void handleAction(RegisterAction registerAction) {
        switch (registerAction.getValue()){
            case RegisterAction.SHOW_LOGIN:
                Intent goToLoginActivityIntent = new Intent(this, LoginActivity.class);
                startActivity(goToLoginActivityIntent);
                break;
            case RegisterAction.SHOW_LOGIN_MENU:
                Intent goToLoginMenuActivityIntent = new Intent(this, LoginMenuActivity.class);
                startActivity(goToLoginMenuActivityIntent);
                break;
        }
    }

    @Override
    public void onSuccess(LiveData<BasicResponse> response) {
        response.observe(this, new Observer<BasicResponse>() {
            @Override
            public void onChanged(BasicResponse result) {
                if(response.getValue() != null) {
                    if ("true".equals(response.getValue().getSuccess())) {
                        Toast.makeText(RegisterActivity.this,"Konto utworzone",Toast.LENGTH_LONG).show();
                        handleAction(new RegisterAction(RegisterAction.SHOW_LOGIN));
                    } else {
                        String errorMessage = response.getValue().getErrorString();
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}