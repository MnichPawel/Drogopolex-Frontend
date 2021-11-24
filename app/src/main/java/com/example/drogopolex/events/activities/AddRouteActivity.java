package com.example.drogopolex.events.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.data.network.response.RouteResponse;
import com.example.drogopolex.databinding.ActivityAddRouteBinding;
import com.example.drogopolex.events.listeners.AddRouteActivityListener;
import com.example.drogopolex.events.utils.AddRouteAction;
import com.example.drogopolex.events.viewModel.AddRouteViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

public class AddRouteActivity extends AppCompatActivity implements SharedPreferencesHolder, AddRouteActivityListener {
    ActivityAddRouteBinding activityAddRouteBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAddRouteBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_route);
        activityAddRouteBinding.setViewModel(new AddRouteViewModel());
        activityAddRouteBinding.executePendingBindings();
        activityAddRouteBinding.getViewModel().sharedPreferencesHolder = this;
        activityAddRouteBinding.getViewModel().addRouteActivityListener = this;

        activityAddRouteBinding.getViewModel().getAction().observe(this, routesListAction -> {
            if (routesListAction != null) {
                handleAction(routesListAction);
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if (!sp.getBoolean("loggedIn", false)) {
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }
    }

    private void handleAction(AddRouteAction addRouteAction) {
        switch (addRouteAction.getValue()) {
            case AddRouteAction.SHOW_ROUTES_LIST:
                Intent goToMapIntent = new Intent(this, MapActivity.class);
                startActivity(goToMapIntent);
                break;
        }
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }

    @Override
    public void onSuccessAddRoute(LiveData<RouteResponse> routeResponseLiveData) {
        routeResponseLiveData.observe(this, routeResponse -> {
//            to implement
        });
    }
}
