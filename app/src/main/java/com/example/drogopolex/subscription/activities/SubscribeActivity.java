package com.example.drogopolex.subscription.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.databinding.ActivitySubscribeBinding;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.subscription.listeners.SubscribeListener;
import com.example.drogopolex.subscription.utils.SubscribeAction;
import com.example.drogopolex.subscription.viewModel.SubscribeViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

public class SubscribeActivity extends AppCompatActivity implements SharedPreferencesHolder, SubscribeListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySubscribeBinding activitySubscribeBinding = DataBindingUtil.setContentView(this, R.layout.activity_subscribe);
        activitySubscribeBinding.setViewModel(new SubscribeViewModel());
        activitySubscribeBinding.executePendingBindings();
        activitySubscribeBinding.getViewModel().sharedPreferencesHolder = this;
        activitySubscribeBinding.getViewModel().subscribeListener = this;

        activitySubscribeBinding.getViewModel().getAction().observe(this, this::handleAction);

        SharedPreferences sp = getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (!sp.getBoolean("loggedIn", false)) {
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }
    }

    private void handleAction(SubscribeAction subscribeAction) {
        if (subscribeAction.getValue() == SubscribeAction.SHOW_SUBSCRIPTIONS) {
            Intent goToSubscriptionsIntent = new Intent(this, SubscriptionsActivity.class);
            startActivity(goToSubscriptionsIntent);
        }
    }

    @Override
    public void onSuccess(LiveData<BasicResponse> response) {
        response.observe(this, result -> {
            if (result != null) {
                if(result.getError() == null) {
                    Toast.makeText(SubscribeActivity.this, "Operacja powiodła się.", Toast.LENGTH_SHORT).show();
                    handleAction(new SubscribeAction(SubscribeAction.SHOW_SUBSCRIPTIONS));
                } else {
                    Toast.makeText(SubscribeActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SubscribeActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }
}