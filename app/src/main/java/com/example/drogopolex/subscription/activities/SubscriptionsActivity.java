package com.example.drogopolex.subscription.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.adapters.SubscriptionsListAdapter;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.response.SubscriptionsResponse;
import com.example.drogopolex.databinding.ActivitySubscriptionsBinding;
import com.example.drogopolex.events.activities.MapActivity;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexSubscription;
import com.example.drogopolex.subscription.listeners.SubscriptionListListener;
import com.example.drogopolex.subscription.utils.SubscriptionsAction;
import com.example.drogopolex.subscription.viewModel.SubscriptionsViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsActivity extends AppCompatActivity implements SharedPreferencesHolder, SubscriptionListListener { //OnSuccessListener<LiveData<SubscriptionsResponse>>,
    RecyclerView subscriptionsRecyclerView;
    ActivitySubscriptionsBinding activitySubscriptionsBinding;
    SubscriptionsListAdapter listAdapter;

    List<DrogopolexSubscription> subscriptions = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySubscriptionsBinding = DataBindingUtil.setContentView(this, R.layout.activity_subscriptions);
        activitySubscriptionsBinding.setViewModel(new SubscriptionsViewModel(getApplication()));
        activitySubscriptionsBinding.executePendingBindings();
        activitySubscriptionsBinding.getViewModel().sharedPreferencesHolder = this;
        activitySubscriptionsBinding.getViewModel().subscriptionListListener = this;


        activitySubscriptionsBinding.getViewModel().getAction().observe(this, subscriptionsAction -> {
            if (subscriptionsAction != null) {
                handleAction(subscriptionsAction);
            }
        });

        subscriptionsRecyclerView = findViewById(R.id.subscriptionsView);

        SharedPreferences sp = getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (!sp.getBoolean("loggedIn", false)) {
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }

        getSubscriptions();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    private void handleAction(SubscriptionsAction subscriptionsAction) {
        switch (subscriptionsAction.getValue()) {
            case SubscriptionsAction.SHOW_MAP:
                Intent goToMapIntent = new Intent(this, MapActivity.class);
                startActivity(goToMapIntent);
                break;
            case SubscriptionsAction.SHOW_SUBSCRIBE:
                Intent goToSubscribeIntent = new Intent(this, SubscribeActivity.class);
                startActivity(goToSubscribeIntent);
                break;
            case SubscriptionsAction.SHOW_SUBSCRIBED_EVENTS:
                Intent goToSubscribedEventsIntent = new Intent(this, SubscribedEventsActivity.class);
                startActivity(goToSubscribedEventsIntent);
                break;
            default:
                Log.e("SubscriptionsActivity", "Unknown action.");
        }
    }

    public void getSubscriptions() {
        activitySubscriptionsBinding.getViewModel().requestSubscriptions();
    }

    @Override
    public void onFailure(String s) {
        Toast.makeText(SubscriptionsActivity.this, "Nie uda??o si?? przetworzy?? odpowiedzi.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(LiveData<SubscriptionsResponse> subscriptionsResponseLiveData) {
        subscriptionsResponseLiveData.observe(this, subscriptionsResponse -> {
            if (subscriptionsResponse != null) {
                subscriptions.clear();
                subscriptionsResponse.getSubscriptions()
                        .forEach(subscription ->
                                subscriptions.add(new DrogopolexSubscription(
                                        Integer.parseInt(subscription.getId()),
                                        subscription.getLocalization(),
                                        Boolean.parseBoolean(subscription.getRec())
                                ))
                        );
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                } else {
                    listAdapter = new SubscriptionsListAdapter(subscriptions, SubscriptionsActivity.this);
                    subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(SubscriptionsActivity.this));
                    subscriptionsRecyclerView.setAdapter(listAdapter);
                }
            } else {
                Toast.makeText(SubscriptionsActivity.this, "Nie uda??o si?? przetworzy?? odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
