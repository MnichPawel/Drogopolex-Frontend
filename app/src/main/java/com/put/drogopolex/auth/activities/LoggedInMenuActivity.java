package com.put.drogopolex.auth.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.put.drogopolex.R;
import com.put.drogopolex.auth.listeners.BasicListener;
import com.put.drogopolex.auth.listeners.SharedPreferencesHolder;
import com.put.drogopolex.auth.utils.LoggedInMenuAction;
import com.put.drogopolex.auth.viewModel.LoggedInMenuViewModel;
import com.put.drogopolex.data.network.response.BasicResponse;
import com.put.drogopolex.databinding.ActivityLoggedInMenuBinding;
import com.put.drogopolex.events.activities.EventsActivity;
import com.put.drogopolex.events.activities.NewEventActivity;
import com.put.drogopolex.subscription.activities.SubscribedEventsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class LoggedInMenuActivity extends AppCompatActivity implements SharedPreferencesHolder, BasicListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoggedInMenuBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_logged_in_menu);
        activityMainBinding.setViewModel(new LoggedInMenuViewModel());
        activityMainBinding.executePendingBindings();
        activityMainBinding.getViewModel().sharedPreferencesHolder = this;
        activityMainBinding.getViewModel().logoutListener = this;

        activityMainBinding.getViewModel().getAction().observe(this, new Observer<LoggedInMenuAction>() {
            @Override
            public void onChanged(LoggedInMenuAction loggedInMenuAction) {
                if(loggedInMenuAction != null){
                    handleAction(loggedInMenuAction);
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }
    }

    private void handleAction(LoggedInMenuAction loggedInMenuAction) {
        switch (loggedInMenuAction.getValue()){
            case LoggedInMenuAction.SHOW_NEW_EVENT:
                Intent goToNewEventActivityIntent = new Intent(this, NewEventActivity.class);
                startActivity(goToNewEventActivityIntent);
                break;
            case LoggedInMenuAction.SHOW_EVENTS:
                Intent goToEventsActivityIntent = new Intent(this, EventsActivity.class);
                startActivity(goToEventsActivityIntent);
                break;
            case LoggedInMenuAction.SHOW_SUBSCRIPTIONS:
                Intent goToSubscribedEventsActivityIntent = new Intent(this, SubscribedEventsActivity.class);
                startActivity(goToSubscribedEventsActivityIntent);
                break;
            case LoggedInMenuAction.SHOW_PROFILE:
                Intent goToProfileActivityIntent = new Intent(this, ProfileActivity.class);
                startActivity(goToProfileActivityIntent);
                break;
            case LoggedInMenuAction.SHOW_LOGIN_MENU:
                Intent goToLoginMenuActivityIntent = new Intent(this, LoginMenuActivity.class);
                startActivity(goToLoginMenuActivityIntent);
                break;
        }
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }

    @Override
    public void onSuccess(LiveData<BasicResponse> response) {
        response.observe(this, new Observer<BasicResponse>() {
            @Override
            public void onChanged(BasicResponse result) {
                if(response.getValue() != null) {
                    if ("true".equals(response.getValue().getSuccess())) {
                        resetSharedPreferences();
                        handleAction(new LoggedInMenuAction(LoggedInMenuAction.SHOW_LOGIN_MENU));
                    } else {
                        resetSharedPreferences();
                        Toast.makeText(LoggedInMenuActivity.this, response.getValue().getErrorString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoggedInMenuActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(LoggedInMenuActivity.this, message, Toast.LENGTH_SHORT).show();
        resetSharedPreferences();
        handleAction(new LoggedInMenuAction(LoggedInMenuAction.SHOW_LOGIN_MENU));
    }

    private void resetSharedPreferences() {
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString("token","");
        spEditor.putString("user_id","");
        spEditor.putBoolean("loggedIn",false);
        spEditor.apply();
    }
}