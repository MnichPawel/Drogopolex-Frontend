package com.example.drogopolex.events.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.databinding.ActivityAddRouteBinding;
import com.example.drogopolex.events.listeners.AddRouteActivityListener;
import com.example.drogopolex.events.utils.AddRouteAction;
import com.example.drogopolex.events.viewModel.AddRouteViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

public class AddRouteActivity extends AppCompatActivity
        implements SharedPreferencesHolder,
        AddRouteActivityListener,
        OnMapReadyCallback {

    ActivityAddRouteBinding activityAddRouteBinding;
    GoogleMap map;
    Dialog fbDialogue = null;

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
            case AddRouteAction.SHOW_POPUP:
                showPopup();
                break;
            case AddRouteAction.CLOSE_POPUP:
                fbDialogue.hide();
                break;
        }
    }

    private void showPopup() {
        if (fbDialogue == null) {
            fbDialogue = new Dialog(AddRouteActivity.this, android.R.style.Theme_Black_NoTitleBar);
            fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
            fbDialogue.setContentView(R.layout.popup_choose_point);
            fbDialogue.setCancelable(true);
            fbDialogue.show();

            Button closeButton = (Button) fbDialogue.findViewById(R.id.close_popup_button);
            closeButton.setOnClickListener(v -> handleAction(new AddRouteAction(AddRouteAction.CLOSE_POPUP)));

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map_popup);

            mapFragment.getMapAsync(this);
        } else {
            map.clear();
            fbDialogue.show();
        }
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }

    @Override
    public void onSuccessAddRoute(LiveData<RouteValue> routeResponseLiveData) {
        routeResponseLiveData.observe(this, routeResponse -> {
            if (routeResponse != null) {
                Toast.makeText(AddRouteActivity.this, "Wyznaczono trasę.", Toast.LENGTH_SHORT).show();
                handleAction(new AddRouteAction(AddRouteAction.SHOW_ROUTES_LIST));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);


        UiSettings mapSettings = map.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setMapToolbarEnabled(false);
    }

}
