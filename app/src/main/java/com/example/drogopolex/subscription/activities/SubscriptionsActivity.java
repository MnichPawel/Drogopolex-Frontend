package com.example.drogopolex.subscription.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.adapters.SubscriptionsListAdapter;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.data.network.response.SubscriptionsResponse;
import com.example.drogopolex.databinding.ActivitySubscriptionsBinding;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexSubscription;
import com.example.drogopolex.subscription.utils.SubscriptionsAction;
import com.example.drogopolex.subscription.viewModel.SubscriptionsViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsActivity extends AppCompatActivity implements SharedPreferencesHolder, OnSuccessListener<LiveData<SubscriptionsResponse>> {
    RecyclerView subscriptionsRecyclerView;
    ActivitySubscriptionsBinding activitySubscriptionsBinding;
    SubscriptionsListAdapter listAdapter;

    ArrayList<DrogopolexSubscription> subscriptions = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySubscriptionsBinding = DataBindingUtil.setContentView(this, R.layout.activity_subscriptions);
        activitySubscriptionsBinding.setViewModel(new SubscriptionsViewModel(getApplication()));
        activitySubscriptionsBinding.executePendingBindings();
        activitySubscriptionsBinding.getViewModel().sharedPreferencesHolder = this;
        activitySubscriptionsBinding.getViewModel().onSuccessListener = this;



        activitySubscriptionsBinding.getViewModel().getAction().observe(this, new Observer<SubscriptionsAction>() {
            @Override
            public void onChanged(SubscriptionsAction subscriptionsAction) {
                if(subscriptionsAction != null){
                    handleAction(subscriptionsAction);
                }
            }
        });

        subscriptionsRecyclerView = findViewById(R.id.subscriptionsView);

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }

        getSubscriptions();
    }
    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }
    private void handleAction(SubscriptionsAction subscriptionsAction) {
        switch (subscriptionsAction.getValue()) {
            case SubscriptionsAction.SHOW_LOGGED_IN:
                Intent goToLoggedInIntent = new Intent(this, LoggedInMenuActivity.class);
                startActivity(goToLoggedInIntent);
                break;
            case SubscriptionsAction.SHOW_SUBSCRIBED_EVENTS:
                Intent goToSubscribedEventsIntent = new Intent(this, SubscribedEventsActivity.class);
                startActivity(goToSubscribedEventsIntent);
        }
    }

    public void getSubscriptions() {
        activitySubscriptionsBinding.getViewModel().requestSubscriptions();
    }

    @Override
    public void onSuccess(LiveData<SubscriptionsResponse> subscriptionsResponseLiveData) {
        subscriptionsResponseLiveData.observe(this, new Observer<SubscriptionsResponse>() {
            @Override
            public void onChanged(SubscriptionsResponse subscriptionsResponse) {
                if(subscriptionsResponse != null) {
                    subscriptions.clear();
                    subscriptionsResponse.getSubscriptions()
                            .forEach(subscription -> {
                                subscriptions.add(new DrogopolexSubscription(
                                        Integer.parseInt(subscription.getId()),
                                        subscription.getLocalization()
                                ));
                            });
                    if(listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    } else {
                        listAdapter = new SubscriptionsListAdapter(subscriptions, SubscriptionsActivity.this);
                        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(SubscriptionsActivity.this));
                        subscriptionsRecyclerView.setAdapter(listAdapter);
                    }
                } else {
                    Toast.makeText(SubscriptionsActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
