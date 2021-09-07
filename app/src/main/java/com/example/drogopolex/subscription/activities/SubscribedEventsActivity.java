package com.example.drogopolex.subscription.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.drogopolex.R;
import com.example.drogopolex.adapters.EventListAdapter;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.databinding.ActivitySubscribedEventsBinding;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.subscription.utils.SubscribedEventsAction;
import com.example.drogopolex.subscription.viewModel.SubscribedEventsViewModel;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribedEventsActivity extends AppCompatActivity implements SharedPreferencesHolder {
    RecyclerView subscribedEventsRecyclerView;
    ActivitySubscribedEventsBinding activitySubscribedEventsBinding;
    EventListAdapter eventListAdapter;
    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySubscribedEventsBinding = DataBindingUtil.setContentView(this, R.layout.activity_subscribed_events);
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");
        activitySubscribedEventsBinding.setViewModel(new SubscribedEventsViewModel(getApplication(), user_id, token));
        activitySubscribedEventsBinding.executePendingBindings();

        subscribedEventsRecyclerView = (RecyclerView) findViewById(R.id.subscribed_events_view);
        activitySubscribedEventsBinding.getViewModel().getAction().observe(this, new Observer<SubscribedEventsAction>() {
        @Override
        public void onChanged(SubscribedEventsAction subscriptionsEventAction) {
            if(subscriptionsEventAction != null){
                handleAction(subscriptionsEventAction);
            }
        }
         });

        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }


        eventListAdapter = new EventListAdapter(eventListData, this);
        subscribedEventsRecyclerView.setAdapter(eventListAdapter);
        subscribedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void handleAction(SubscribedEventsAction subscribedEventsAction) {
        switch (subscribedEventsAction.getValue()) {
            case SubscribedEventsAction.ADD_SUBSCRIPTION:
                Intent goToAddSubIntent = new Intent(this, SubscribeActivity.class);
                startActivity(goToAddSubIntent);
                break;
            case SubscribedEventsAction.SHOW_SUBSCRIPTIONS:
                Intent goToSubscriptionsIntent = new Intent(this, SubscriptionsActivity.class);
                startActivity(goToSubscriptionsIntent);
                break;
            case SubscribedEventsAction.GO_TO_MAIN_MENU:
                Intent goToMainMenuIntent = new Intent(this, LoggedInMenuActivity.class);
                startActivity(goToMainMenuIntent);
        }
    }
    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
        startActivity(goToMainActivityIntent);
    }
}