package com.example.drogopolex.auth.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.listeners.BasicListener;
import com.example.drogopolex.auth.listeners.SharedPreferencesHolder;
import com.example.drogopolex.auth.utils.ProfileAction;
import com.example.drogopolex.auth.viewModel.ProfileViewModel;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.databinding.ActivityProfileBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class ProfileActivity extends AppCompatActivity implements SharedPreferencesHolder, BasicListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        activityProfileBinding.setViewModel(new ProfileViewModel());
        activityProfileBinding.executePendingBindings();
        activityProfileBinding.getViewModel().sharedPreferencesHolder = this;
        activityProfileBinding.getViewModel().basicListener = this;

        activityProfileBinding.getViewModel().getAction().observe(this, new Observer<ProfileAction>() {
            @Override
            public void onChanged(ProfileAction profileAction) {
                if(profileAction != null){
                    handleAction(profileAction);
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }
    }

    private void handleAction(ProfileAction profileAction) {
        if (ProfileAction.SHOW_LOGGED_IN == profileAction.getValue()) {
            Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
            startActivity(goToLoggedInMenuActivityIntent);
        }
    }

    @Override
    public void onSuccess(LiveData<BasicResponse> response) {
        response.observe(this, new Observer<BasicResponse>() {
            @Override
            public void onChanged(BasicResponse result) {
                if(response.getValue() != null) {
                    if ("true".equals(response.getValue().getSuccess())) {
                        Toast.makeText(ProfileActivity.this, "Operacja powiodła się.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, response.getValue().getErrorString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }
}