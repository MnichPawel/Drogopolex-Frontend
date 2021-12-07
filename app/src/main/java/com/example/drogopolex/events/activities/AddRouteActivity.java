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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

import static com.example.drogopolex.constants.AppConstant.PERMISSIONS_REQUEST_LOCATION;

public class AddRouteActivity extends AppCompatActivity
        implements SharedPreferencesHolder,
        AddRouteActivityListener,
        OnMapReadyCallback {

    ActivityAddRouteBinding activityAddRouteBinding;
    GoogleMap map;
    Dialog fbDialogue = null;
    private Marker chosenPointMarker = null;

    LatLng chosenSourceLatLng = null;
    LatLng chosenDestinationLatLng = null;

    boolean firstLocalizationUpdateLoaded = false;
    boolean isChooseSourceMode = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAddRouteBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_route);
        activityAddRouteBinding.setViewModel(new AddRouteViewModel(getApplication()));
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

    @Override
    protected void onDestroy() {
        fbDialogue.dismiss();
        super.onDestroy();
    }

    private void handleAction(AddRouteAction addRouteAction) {
        switch (addRouteAction.getValue()) {
            case AddRouteAction.SHOW_ROUTES_LIST:
                Intent goToRoutesIntent = new Intent(this, RoutesListActivity.class);
                startActivity(goToRoutesIntent);
                break;
            case AddRouteAction.CHOOSE_SOURCE_POINT:
                isChooseSourceMode = true;
                showPopup();
                break;
            case AddRouteAction.CHOOSE_DESTINATION_POINT:
                isChooseSourceMode = false;
                showPopup();
                break;
            case AddRouteAction.ACCEPT_POPUP:
                updateEditText(chosenPointMarker.getPosition());
                fbDialogue.hide();
                break;
            case AddRouteAction.CANCEL_POPUP:
                fbDialogue.hide();
                break;
        }
    }

    private void updateEditText(LatLng latLng) {
        if (isChooseSourceMode) {
            activityAddRouteBinding.editTextSource.setText(latLng.toString());
            chosenSourceLatLng = latLng;
        } else {
            activityAddRouteBinding.editTextDestination.setText(latLng.toString());
            chosenDestinationLatLng = latLng;
        }
    }

    private void showPopup() {
        if (fbDialogue == null) {
            fbDialogue = new Dialog(AddRouteActivity.this, android.R.style.Theme_Black_NoTitleBar);
            fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
            fbDialogue.setContentView(R.layout.popup_choose_point);
            fbDialogue.setCancelable(true);
            fbDialogue.show();

            Button closeButton = (Button) fbDialogue.findViewById(R.id.cancel_popup_button);
            closeButton.setOnClickListener(v -> handleAction(new AddRouteAction(AddRouteAction.CANCEL_POPUP)));
            Button acceptButton = (Button) fbDialogue.findViewById(R.id.accept_popup_button);
            acceptButton.setOnClickListener(v -> handleAction(new AddRouteAction(AddRouteAction.ACCEPT_POPUP)));

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map_popup);

            mapFragment.getMapAsync(this);
        } else {
            map.clear();
            fbDialogue.show();
        }
    }

    private void prepRequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSIONS_REQUEST_LOCATION);
        } else {
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        activityAddRouteBinding.getViewModel().getLocationLiveData()
                .observe(this, locationDetails -> {
                    if (!firstLocalizationUpdateLoaded) {
                        firstLocalizationUpdateLoaded = true;
                        LatLng location = new LatLng(
                                Double.parseDouble(locationDetails.getLatitude()),
                                Double.parseDouble(locationDetails.getLongitude()));

                        Toast.makeText(getApplicationContext(), location.latitude + " - " + location.longitude, Toast.LENGTH_SHORT).show();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
                        putMarkerOnMap(location);
                    }
                });
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
    public LatLng getLatLngOfChosenPoint(boolean isSource) {
        if (isSource)
            return chosenSourceLatLng;
        else
            return chosenDestinationLatLng;
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

        prepRequestLocationUpdates();
    }

    private void putMarkerOnMap(LatLng latLng) {
        MarkerOptions chosenPointMarkerOptions = new MarkerOptions()
                .position(latLng)
                .title("Cel");

        chosenPointMarker = map.addMarker(chosenPointMarkerOptions);

        map.setOnMapClickListener(pos -> {
            chosenPointMarkerOptions.position(pos);
            chosenPointMarker.remove();
            chosenPointMarker = map.addMarker(chosenPointMarkerOptions);
        });
    }
}