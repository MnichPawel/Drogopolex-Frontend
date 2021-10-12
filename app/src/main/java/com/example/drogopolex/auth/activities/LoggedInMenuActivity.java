package com.example.drogopolex.auth.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.utils.LoggedInMenuAction;
import com.example.drogopolex.auth.viewModel.LoggedInMenuViewModel;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.databinding.ActivityLoggedInMenuBinding;
import com.example.drogopolex.events.activities.EventsActivity;
import com.example.drogopolex.events.activities.MapActivity;
import com.example.drogopolex.events.activities.NewEventActivity;
import com.example.drogopolex.listeners.BasicListener;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.subscription.activities.SubscribedEventsActivity;
import com.example.drogopolex.utils.SharedPreferencesUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class LoggedInMenuActivity extends AppCompatActivity implements SharedPreferencesHolder, BasicListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoggedInMenuBinding activityLoggedInMenuBinding = DataBindingUtil.setContentView(this, R.layout.activity_logged_in_menu);
        activityLoggedInMenuBinding.setViewModel(new LoggedInMenuViewModel());
        activityLoggedInMenuBinding.executePendingBindings();
        activityLoggedInMenuBinding.getViewModel().sharedPreferencesHolder = this;
        activityLoggedInMenuBinding.getViewModel().logoutListener = this;

        activityLoggedInMenuBinding.getViewModel().getAction().observe(this, new Observer<LoggedInMenuAction>() {
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
            case LoggedInMenuAction.SHOW_MAP:
                Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
                startActivity(goToMapActivityIntent);
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
                if(result != null) {
                    if ("true".equals(result.getSuccess())) {
                        SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
                        handleAction(new LoggedInMenuAction(LoggedInMenuAction.SHOW_LOGIN_MENU));
                    } else {
                        SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
                        Toast.makeText(LoggedInMenuActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
                        handleAction(new LoggedInMenuAction(LoggedInMenuAction.SHOW_LOGIN_MENU));
                    }
                } else {
                    Toast.makeText(LoggedInMenuActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                    SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
                    handleAction(new LoggedInMenuAction(LoggedInMenuAction.SHOW_LOGIN_MENU));
                }
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(LoggedInMenuActivity.this, message, Toast.LENGTH_SHORT).show();
        SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
        handleAction(new LoggedInMenuAction(LoggedInMenuAction.SHOW_LOGIN_MENU));
    }


}