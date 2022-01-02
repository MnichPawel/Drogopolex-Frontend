package com.example.drogopolex.events.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.auth.activities.ProfileActivity;
import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.LocationResponse;
import com.example.drogopolex.data.network.response.PointsOfInterestResponse;
import com.example.drogopolex.data.network.response.PointsOfInterestValue;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.databinding.ActivityMapBinding;
import com.example.drogopolex.events.listeners.MapActivityListener;
import com.example.drogopolex.events.utils.IconUtils;
import com.example.drogopolex.events.utils.MapAction;
import com.example.drogopolex.events.viewModel.MapViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.subscription.activities.SubscriptionsActivity;
import com.example.drogopolex.utils.CoordinatesUtils;
import com.example.drogopolex.utils.SharedPreferencesUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.internal.LinkedTreeMap;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import static com.example.drogopolex.constants.AppConstant.PERMISSIONS_REQUEST_LOCATION;

public class MapActivity extends FragmentActivity
        implements OnMapReadyCallback,
        MapActivityListener,
        SharedPreferencesHolder,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener {

    private ActivityMapBinding activityMapBinding;
    private GoogleMap map;

    /* Constants */
    private static final String CAMERA_TAG = "CAMERA";
    private static final String UNKNOWN_MESSAGE = "Nie udało się przetworzyć odpowiedzi.";

    /* Camera state flags */
    private boolean moveCameraToUser = true;
    private boolean userCameraMove = false;

    /* Map assets to restore after reset */
    private final ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    private MarkerOptions routeDestinationMarkerOptions = null;
    private Marker routeDestinationMarker = null;
    private List<PointsOfInterestValue> pointsOfInterest = null;
    private JSONObject routeGeoJson = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        activityMapBinding.setViewModel(new MapViewModel(getApplication()));
        activityMapBinding.executePendingBindings();
        activityMapBinding.getViewModel().mapActivityListener = this;
        activityMapBinding.getViewModel().sharedPreferencesHolder = this;

        activityMapBinding.getViewModel().getAction().observe(this, eventsAction -> {
            if (eventsAction != null) {
                handleAction(eventsAction);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void handleAction(MapAction aMapAction) {
        switch (aMapAction.getValue()) {
            case MapAction.SHOW_SUBSCRIPTIONS:
                Intent goToSubscriptionsIntent = new Intent(this, SubscriptionsActivity.class);
                startActivity(goToSubscriptionsIntent);
                break;
            case MapAction.SHOW_PROFILE:
                Intent goToProfileIntent = new Intent(this, ProfileActivity.class);
                startActivity(goToProfileIntent);
                break;
            case MapAction.SHOW_ROUTES_LIST:
                Intent goToRoutesListIntent = new Intent(this, RoutesListActivity.class);
                startActivity(goToRoutesListIntent);
                break;
            case MapAction.LOGOUT:
                Intent goToLoginMenuIntent = new Intent(this, LoginMenuActivity.class);
                startActivity(goToLoginMenuIntent);
                break;
            case MapAction.RESET_ROUTE:
                resetMap();
                break;
            default:
                Toast.makeText(MapActivity.this, "Nieznana akcja", Toast.LENGTH_SHORT).show();
        }
    }

    /* Shared Prefences Holder */
    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    /* Location */
    private void prepRequestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {

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
        activityMapBinding.getViewModel().getLocationLiveData().observe(this, locationDetails -> {
            Log.d(CAMERA_TAG, "new location received");

            if (moveCameraToUser) {
                LatLng location = new LatLng(
                        Double.parseDouble(locationDetails.getLatitude()),
                        Double.parseDouble(locationDetails.getLongitude()));
                moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
            }
        });
    }

    /* Map */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setTrafficEnabled(true);
        map.setOnMarkerClickListener(this);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
        map.setOnCameraMoveStartedListener(this::onCameraMoveStarted);
        map.setOnCameraIdleListener(this::onCameraIdle);

        UiSettings mapSettings = map.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setMapToolbarEnabled(false);

        mapSettings.setMyLocationButtonEnabled(true);
        changePositionOfMyLocationButton();

        if (getIntent().hasExtra("routeId")) {
            String selectedRouteId = (String) getIntent().getSerializableExtra("routeId");
            activityMapBinding.getViewModel().getRouteById(selectedRouteId);
        }

        prepRequestLocationUpdates();
    }

    private void resetMapEvents() {
        eventListData.clear();
        map.clear();

        restoreDestinationMarker();
        restoreDisplayedRoute();
        restorePointsOfInterest();
    }

    private void restorePointsOfInterest() {
        if (pointsOfInterest != null)
            pointsOfInterest.forEach(poi -> {
                LatLng coordinates = CoordinatesUtils.parseCoordinatesString(poi.getCoordinates());
                addPOIToMap(coordinates, poi.getName(), poi.getCategory_name());
            });
    }

    private void restoreDisplayedRoute() {
        if (routeGeoJson != null)
            drawRouteOnMap(routeGeoJson);
    }

    private void restoreDestinationMarker() {
        if (routeDestinationMarkerOptions != null)
            routeDestinationMarker = map.addMarker(routeDestinationMarkerOptions);
    }

    @Override
    public LatLngBounds getMapBounds() {
        return map.getProjection().getVisibleRegion().latLngBounds;
    }

    private void drawRouteOnMap(JSONObject geoJson) {
        GeoJsonLayer geoJsonLayer = new GeoJsonLayer(map, geoJson);
        geoJsonLayer.addLayerToMap();
    }

    private void resetMap() {
        routeDestinationMarkerOptions = null;
        routeDestinationMarker = null;
        routeGeoJson = null;
        map.clear();

        restorePointsOfInterest();

        for (DrogopolexEvent event : eventListData) {
            String snippetMsg = event.getId()
                    + "," + event.getUserVoteType() + "," + event.getValueOfVotes();
            addEventToMap(event.getCoordinates(), event.getType(), snippetMsg);
        }
    }

    private void addEventToMap(LatLng coordinates, String type, String snippetMsg) {
        map.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(type)
                .snippet(snippetMsg)
                .icon(IconUtils.findIconForType(type, this)));
    }

    private void addPOIToMap(LatLng coordinates, String name, String type) {
        map.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(name)
                .snippet("notEvent")
                .icon(IconUtils.findIconForType(type, this)));
    }

    /* Camera */
    private void onCameraMoveStarted(int i) {
        Log.d(CAMERA_TAG, "move started");
        if (userCameraMove) {
            moveCameraToUser = false;
        } else {
            userCameraMove = true;
            moveCameraToUser = true;
        }
    }

    private void onCameraIdle() {
        Log.d(CAMERA_TAG, "camera idle");
        LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
        activityMapBinding.getViewModel().onLocationChanged(latLngBounds);
    }

    private void moveCameraToBbox(LocationResponse bboxStartResponse, LocationResponse bboxEndResponse) {
        LatLng bboxStart = new LatLng(bboxStartResponse.getLat(), bboxStartResponse.getLng());
        LatLng bboxEnd = new LatLng(bboxEndResponse.getLat(), bboxEndResponse.getLng());

        LatLngBounds bbox = new LatLngBounds(bboxStart, bboxEnd);
        moveCamera(CameraUpdateFactory.newLatLngBounds(bbox, 0));
    }

    private void moveCamera(CameraUpdate cameraUpdate) {
        userCameraMove = false;
        Log.d(CAMERA_TAG, "app will move");
        map.moveCamera(cameraUpdate);
    }

    /* View */
    private void changePositionOfMyLocationButton() { //TODO better location of button
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(CAMERA_TAG, "MyLocationButton clicked.");
        userCameraMove = false;
        return false;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        marker.hideInfoWindow();
        if (marker.getSnippet() != null && !marker.getSnippet().equals("notEvent"))
            VotesPopup.showVotePopup(this, activityMapBinding.getRoot(), marker);
        return true;
    }

    /* Choose point mode */
    @Override
    public void onChoosePointModeEntered(LatLng location) {
        routeDestinationMarkerOptions = new MarkerOptions()
                .position(location)
                .title("Cel");

        routeDestinationMarker = map.addMarker(routeDestinationMarkerOptions);

        map.setOnMapClickListener(latLng -> {
            routeDestinationMarkerOptions.position(latLng);
            routeDestinationMarker.remove();
            routeDestinationMarker = map.addMarker(routeDestinationMarkerOptions);
        });
    }

    @Override
    public LatLng getChosenPoint() {
        return routeDestinationMarker.getPosition();
    }

    /* Route recommendation */
    @Override
    public void recommendRoute(LiveData<RouteValue> routeRec) {
        routeRec.observe(this, routeValue -> {
            if (routeRec.getValue() != null) {
                showRecommendedRoutePopup(routeRec.getValue());
            }
        });
    }

    public void showRecommendedRoutePopup(RouteValue routeValue) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_route_recommendation, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        activityMapBinding.getViewModel().setFirstLoginToFalse();
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.setElevation(20);

        TextView routeToTextView = popupView.findViewById(R.id.placeNameTo);
        String routeRecommendationText = "DO: " + routeValue.getTo().getName();
        routeToTextView.setText(routeRecommendationText);

        Button acceptBtn = popupView.findViewById(R.id.accept_rule_by_name_popup_button);
        Button cancelBtn = popupView.findViewById(R.id.cancel_rule_by_name_popup_button);

        popupWindow.showAtLocation(activityMapBinding.getRoot(), Gravity.CENTER, 0, 0);

        acceptBtn.setOnClickListener(v -> {
            activityMapBinding.getViewModel().getRouteFromLocToPoint(routeValue.getTo().getLat(), routeValue.getTo().getLng());
            popupWindow.dismiss();
        });
        cancelBtn.setOnClickListener(v -> popupWindow.dismiss());
    }

    /* Request response callbacks */
    @Override
    public void onAddNewEventSuccess(LiveData<BasicResponse> response) {
        response.observe(this, basicResponse -> {
            if (basicResponse != null) {
                if (basicResponse.getError() == null) {
                    Toast.makeText(MapActivity.this, "Operacja powiodła się.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, basicResponse.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MapActivity.this, UNKNOWN_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGetEventsSuccess(LiveData<EventsResponse> eventsResponseLiveData) {
        eventsResponseLiveData.observe(this, eventsResponse -> {
            if (eventsResponse != null) {
                resetMapEvents();

                eventsResponse.getEvents()
                        .forEach(event -> {
                            DrogopolexEvent drogopolexEvent = new DrogopolexEvent(event);
                            eventListData.add(drogopolexEvent);

                            String snippetMsg = drogopolexEvent.getId() + ","
                                    + drogopolexEvent.getUserVoteType().getValue() + ","
                                    + drogopolexEvent.getValueOfVotes();
                            addEventToMap(drogopolexEvent.getCoordinates(), drogopolexEvent.getType(), snippetMsg);
                        });

            } else {
                Toast.makeText(MapActivity.this, UNKNOWN_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLogoutSuccess(LiveData<BasicResponse> responseLiveData) {
        responseLiveData.observe(this, result -> {
            if (result == null) {
                Toast.makeText(MapActivity.this, UNKNOWN_MESSAGE, Toast.LENGTH_SHORT).show();
            } else if (result.getError() != null) {
                Toast.makeText(MapActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
            }
            SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE));
            handleAction(new MapAction(MapAction.LOGOUT));
        });
    }

    @Override
    public void onLogoutFailure(String message) {
        Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
        SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences(AppConstant.DROGOPOLEX_SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        handleAction(new MapAction(MapAction.LOGOUT));
    }

    @Override
    public void drawRoute(LiveData<RouteValue> routeResponseLiveData) {
        map.setOnMapClickListener(null);

        routeResponseLiveData.observe(this, routeResponse -> {
            Log.d("DRAW_ROUTE", "observe");
            if (routeResponse != null) {
                Log.d("DRAW_ROUTE", "response not null");
                routeGeoJson = new JSONObject((LinkedTreeMap) routeResponse.getGeometria());
                drawRouteOnMap(routeGeoJson);
                moveCameraToBbox(
                        routeResponse.getBboxStart(),
                        routeResponse.getBboxKoniec()
                );
            }
        });
    }

    @Override
    public void onGetPOISuccess(LiveData<PointsOfInterestResponse> pointsOfInterestResponseLiveData) {
        pointsOfInterestResponseLiveData.observe(this, pointsOfInterestResponse -> {
            if (pointsOfInterestResponseLiveData.getValue() != null) {
                if (pointsOfInterest == null) {
                    pointsOfInterest = pointsOfInterestResponseLiveData.getValue().getPois();

                    restorePointsOfInterest();
                } else {
                    pointsOfInterest = pointsOfInterestResponseLiveData.getValue().getPois();
                }
            }
        });
    }
}
