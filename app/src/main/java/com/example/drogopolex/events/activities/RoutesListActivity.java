package com.example.drogopolex.events.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.databinding.ActivityRoutesListBinding;
import com.example.drogopolex.events.utils.RoutesListAction;
import com.example.drogopolex.events.viewModel.RoutesListViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class RoutesListActivity extends AppCompatActivity {
    ActivityRoutesListBinding activityRoutesListBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRoutesListBinding = DataBindingUtil.setContentView(this, R.layout.activity_routes_list);
        activityRoutesListBinding.setViewModel(new RoutesListViewModel());
        activityRoutesListBinding.executePendingBindings();

        activityRoutesListBinding.getViewModel().getAction().observe(this, routesListAction -> {
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

    private void handleAction(RoutesListAction routesListAction) {
        switch (routesListAction.getValue()) {
            case RoutesListAction.SHOW_MAP:
                Intent goToMapIntent = new Intent(this, MapActivity.class);
                startActivity(goToMapIntent);
                break;
            case RoutesListAction.SHOW_ADD_ROUTE:
                Intent goToAddRouteIntent = new Intent(this, AddRouteActivity.class);
                startActivity(goToAddRouteIntent);
                break;
        }
    }
}
