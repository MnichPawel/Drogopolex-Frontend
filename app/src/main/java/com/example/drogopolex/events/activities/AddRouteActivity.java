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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.adapters.RulesListAdapter;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.databinding.ActivityAddRouteBinding;
import com.example.drogopolex.events.listeners.AddRouteActivityListener;
import com.example.drogopolex.events.utils.AddRouteAction;
import com.example.drogopolex.events.utils.AddRuleAction;
import com.example.drogopolex.events.viewModel.AddRouteViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.rules.DrogopolexEventTypeRule;
import com.example.drogopolex.model.rules.DrogopolexNameRule;
import com.example.drogopolex.model.rules.DrogopolexPointRule;
import com.example.drogopolex.model.rules.DrogopolexRule;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import static com.example.drogopolex.constants.AppConstant.PERMISSIONS_REQUEST_LOCATION;

public class AddRouteActivity extends AppCompatActivity
        implements SharedPreferencesHolder,
        AddRouteActivityListener,
        OnMapReadyCallback {

    RulesListAdapter listAdapter;
    List<DrogopolexRule> drogopolexRules = new ArrayList<>();

    ActivityAddRouteBinding activityAddRouteBinding;
    GoogleMap map;
    MapView mapView;
    LatLng currentLocation;
    Dialog choosePointDialog = null;
    Dialog addRuleDialog = null;
    PopupWindow addRuleByEventTypePopup;
    String[] rules = {
            AddRuleAction.AVOID_BY_POINT.getValue(),
            AddRuleAction.AVOID_EVENT_TYPE.getValue(),
            AddRuleAction.NAVIGATE_THROUGH_BY_NAME.getValue(),
            AddRuleAction.NAVIGATE_THROUGH_BY_POINT.getValue()
    };
    LatLng chosenSourceLatLng = null;
    LatLng chosenDestinationLatLng = null;
    boolean firstLocalizationUpdateLoaded = false;
    int choosePointPopupRole = -1;
    private Marker chosenPointMarker = null;

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

        listAdapter = new RulesListAdapter(drogopolexRules);
        activityAddRouteBinding.rulesView.setLayoutManager(new LinearLayoutManager(AddRouteActivity.this));
        activityAddRouteBinding.rulesView.setAdapter(listAdapter);

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if (!sp.getBoolean("loggedIn", false)) {
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }
    }

    @Override
    protected void onDestroy() {
        choosePointDialog.dismiss();
        super.onDestroy();
    }

    private void handleAction(AddRouteAction addRouteAction) {
        switch (addRouteAction.getValue()) {
            case AddRouteAction.SHOW_ROUTES_LIST:
                Intent goToRoutesIntent = new Intent(this, RoutesListActivity.class);
                startActivity(goToRoutesIntent);
                break;
            case AddRouteAction.CHOOSE_SOURCE_POINT:
            case AddRouteAction.CHOOSE_DESTINATION_POINT:
            case AddRouteAction.AVOID_BY_POINT:
            case AddRouteAction.NAVIGATE_THROUGH_BY_POINT:
                choosePointPopupRole = addRouteAction.getValue();
                showChoosePointPopup();
                break;
            case AddRouteAction.ACCEPT_POPUP:
                onPointChosen(chosenPointMarker.getPosition());
                choosePointDialog.hide();
                break;
            case AddRouteAction.CANCEL_POPUP:
                choosePointDialog.hide();
                break;
            case AddRouteAction.SHOW_ADD_RULE_POPUP:
                showAddRulePopup();
                break;
        }
    }

    private void showAddRulePopup() {
        if (addRuleDialog == null) {
            addRuleDialog = new Dialog(AddRouteActivity.this, android.R.style.Theme_Black_NoTitleBar);
            addRuleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
            addRuleDialog.setContentView(R.layout.popup_add_rule);
            addRuleDialog.setCancelable(true);
            addRuleDialog.show();

            ListView addRuleListView = addRuleDialog.findViewById(R.id.addRuleListView);
            addRuleListView.setAdapter(new ArrayAdapter<>(this, R.layout.row_add_rule, Arrays.asList(rules)));
            addRuleListView.setOnItemClickListener((adapterView, view, i, l) -> handleAddRuleListItemClick(i));
        } else {
            addRuleDialog.show();
        }
    }

    private void handleAddRuleListItemClick(int i) {
        String action = rules[i];
        if (action.equals(AddRuleAction.NAVIGATE_THROUGH_BY_NAME.getValue())) {
            showAddRuleByNamePopup();
        } else if (action.equals(AddRuleAction.AVOID_BY_POINT.getValue())) {
            handleAction(new AddRouteAction(AddRouteAction.AVOID_BY_POINT));
        } else if (action.equals(AddRuleAction.AVOID_EVENT_TYPE.getValue())) {
            showAddRuleByEventTypePopup();
        } else if (action.equals(AddRuleAction.NAVIGATE_THROUGH_BY_POINT.getValue())) {
            handleAction(new AddRouteAction(AddRouteAction.NAVIGATE_THROUGH_BY_POINT));
        }
        addRuleDialog.dismiss();
    }

    private void onPointChosen(LatLng latLng) {
        if (AddRouteAction.CHOOSE_SOURCE_POINT == choosePointPopupRole) {
            activityAddRouteBinding.editTextSource.setText(latLng.toString());
            chosenSourceLatLng = latLng;
        } else if (AddRouteAction.CHOOSE_DESTINATION_POINT == choosePointPopupRole) {
            activityAddRouteBinding.editTextDestination.setText(latLng.toString());
            chosenDestinationLatLng = latLng;
        } else if (AddRouteAction.AVOID_BY_POINT == choosePointPopupRole) {
            drogopolexRules.add(new DrogopolexPointRule(true, "omijaj " + latLng.toString(), latLng));
            listAdapter.notifyItemInserted(drogopolexRules.size() - 1);
        } else if (AddRouteAction.NAVIGATE_THROUGH_BY_POINT == choosePointPopupRole) {
            drogopolexRules.add(new DrogopolexPointRule(false, "prowadź przez " + latLng.toString(), latLng));
            listAdapter.notifyItemInserted(drogopolexRules.size() - 1);
        }
    }

    private void showChoosePointPopup() {
        if (choosePointDialog == null) {
            choosePointDialog = new Dialog(AddRouteActivity.this, android.R.style.Theme_Black_NoTitleBar);
            choosePointDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
            choosePointDialog.setContentView(R.layout.popup_choose_point);
            choosePointDialog.setCancelable(true);
            choosePointDialog.show();

            Button closeButton = choosePointDialog.findViewById(R.id.cancel_popup_button);
            closeButton.setOnClickListener(v -> handleAction(new AddRouteAction(AddRouteAction.CANCEL_POPUP)));
            Button acceptButton = choosePointDialog.findViewById(R.id.accept_popup_button);
            acceptButton.setOnClickListener(v -> handleAction(new AddRouteAction(AddRouteAction.ACCEPT_POPUP)));

            mapView = (MapView) choosePointDialog.findViewById(R.id.map_popup);
            MapsInitializer.initialize(AddRouteActivity.this);

            mapView.onCreate(choosePointDialog.onSaveInstanceState());
            mapView.onResume();
            mapView.getMapAsync(this);
        } else {
            map.clear();
            mapView.onResume();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
            putMarkerOnMap(currentLocation);
            choosePointDialog.show();
        }
    }

    public void showAddRuleByNamePopup() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_add_rule_by_name, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.setElevation(20);

        EditText placeNameEditText = popupView.findViewById(R.id.placeNameEditText);

        Button acceptBtn = popupView.findViewById(R.id.accept_rule_by_name_popup_button);
        Button cancelBtn = popupView.findViewById(R.id.cancel_rule_by_name_popup_button);

        popupWindow.showAtLocation(activityAddRouteBinding.getRoot(), Gravity.CENTER, 0, 0);

        acceptBtn.setOnClickListener(v -> {
            String placeName = placeNameEditText.getText().toString();
            drogopolexRules.add(new DrogopolexNameRule(false, "prowadź przez " + placeName, placeName));
            listAdapter.notifyItemInserted(drogopolexRules.size() - 1);
            popupWindow.dismiss();
        });
        cancelBtn.setOnClickListener(v -> popupWindow.dismiss());
    }

    public void showAddRuleByEventTypePopup() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_add_rule_event_type, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        addRuleByEventTypePopup = new PopupWindow(popupView, width, height, true);

        addRuleByEventTypePopup.setElevation(20);

        addRuleByEventTypePopup.showAtLocation(activityAddRouteBinding.getRoot(), Gravity.CENTER, 0, 0);
    }

    public void onAddRuleByEventTypeRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_korek:
                if (checked)
                    addAvoidEventTypeRule("omijaj korki", "Korek");
                break;
            case R.id.radio_radar:
                if (checked)
                    addAvoidEventTypeRule("omijaj radary", "Radar");
                break;
            case R.id.radio_roboty:
                if (checked)
                    addAvoidEventTypeRule("omijaj roboty drogowe", "Roboty drogowe");
                break;
            case R.id.radio_wypadek:
                if (checked)
                    addAvoidEventTypeRule("omijaj wypadki", "Wypadek");
                break;
        }
    }

    private void addAvoidEventTypeRule(String description, String eventType) {
        drogopolexRules.add(new DrogopolexEventTypeRule(description, eventType));
        listAdapter.notifyItemInserted(drogopolexRules.size() - 1);
        addRuleByEventTypePopup.dismiss();
    }

    private void prepRequestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                        currentLocation = location;
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
    public List<DrogopolexRule> getRules() {
        return drogopolexRules;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));

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
