package com.example.drogopolex.events.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.auth.activities.ProfileActivity;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.databinding.ActivityMapBinding;
import com.example.drogopolex.events.listeners.MapActivityListener;
import com.example.drogopolex.events.utils.MapAction;
import com.example.drogopolex.events.viewModel.MapViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.VoteType;
import com.example.drogopolex.subscription.activities.SubscriptionsActivity;
import com.example.drogopolex.utils.SharedPreferencesUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.internal.LinkedTreeMap;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import static com.example.drogopolex.constants.AppConstant.PERMISSIONS_REQUEST_LOCATION;

public class MapActivity extends FragmentActivity
        implements OnMapReadyCallback,
        MapActivityListener,
        SharedPreferencesHolder,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    GoogleMap map;
    ActivityMapBinding activityMapBinding;

    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    boolean firstLocalizationUpdateLoaded = false;
    boolean displayingSelectedRoute = false;

    private MarkerOptions routeDestinationMarkerOptions = null;
    private Marker routeDestinationMarker = null;

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setTrafficEnabled(true);


        UiSettings mapSettings = map.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setMapToolbarEnabled(false);

        mapSettings.setMyLocationButtonEnabled(true);
        changePositionOfMyLocationButton();

        if (getIntent().hasExtra("routeId")) {
            displayingSelectedRoute = true;
            String selectedRouteId = (String) getIntent().getSerializableExtra("routeId");
            activityMapBinding.getViewModel().getRouteById(selectedRouteId);
        }

        prepRequestLocationUpdates();
    }

    private void changePositionOfMyLocationButton() { //TODO better location of button
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
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
        activityMapBinding.getViewModel().getLocationLiveData().observe(this, locationDetails -> {
            boolean isNearbyEvents = activityMapBinding.getViewModel().onLocationChanged(locationDetails);

            if (!firstLocalizationUpdateLoaded || isNearbyEvents || !displayingSelectedRoute) {
                firstLocalizationUpdateLoaded = true;
                LatLng location = new LatLng(
                        Double.parseDouble(locationDetails.getLatitude()),
                        Double.parseDouble(locationDetails.getLongitude()));
                Toast.makeText(getApplicationContext(), location.latitude + " - " + location.longitude, Toast.LENGTH_SHORT).show();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
            }
        });
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }

    @Override
    public void onAddNewEventSuccess(LiveData<BasicResponse> response) {
        response.observe(this, basicResponse -> {
            if (basicResponse != null) {
                if ("true".equals(basicResponse.getSuccess())) {
                    Toast.makeText(MapActivity.this, "Operacja powiodła się.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = basicResponse.getError();
                    Toast.makeText(MapActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MapActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGetEventsSuccess(LiveData<EventsResponse> eventsResponseLiveData) {
        eventsResponseLiveData.observe(this, eventsResponse -> {
            if (eventsResponse != null) {
                eventListData.clear();
                map.clear();
                if (routeDestinationMarkerOptions != null)
                    routeDestinationMarker = map.addMarker(routeDestinationMarkerOptions);
                if (routeGeoJson != null)
                    drawRouteOnMap(routeGeoJson);
                eventsResponse.getEvents()
                        .forEach(event -> {
                            VoteType userVoteType;
                            if ("1".equals(event.getUserVote())) userVoteType = VoteType.UPVOTED;
                            else if ("-1".equals(event.getUserVote()))
                                userVoteType = VoteType.DOWNVOTED;
                            else userVoteType = VoteType.NO_VOTE;

                            LatLng coordinates = parseCoordinatesString(event.getCoordinates());

                            eventListData.add(new DrogopolexEvent(
                                    event.getType(),
                                    event.getCountry(),
                                    event.getStreet(),
                                    Integer.parseInt(event.getId()),
                                    coordinates,
                                    Integer.parseInt(event.getValueOfVotes()),
                                    userVoteType
                            ));

                            addEventToMap(coordinates, event.getType());
                        });

            } else {
                Toast.makeText(MapActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLogoutSuccess(LiveData<BasicResponse> responseLiveData) {
        responseLiveData.observe(this, result -> {
            if (result != null) {
                if ("true".equals(result.getSuccess())) {
                    SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
                    handleAction(new MapAction(MapAction.LOGOUT));
                } else {
                    SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
                    Toast.makeText(MapActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
                    handleAction(new MapAction(MapAction.LOGOUT));
                }
            } else {
                Toast.makeText(MapActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
                handleAction(new MapAction(MapAction.LOGOUT));
            }
        });
    }

    @Override
    public void onLogoutFailure(String message) {
        Toast.makeText(MapActivity.this, message, Toast.LENGTH_SHORT).show();
        SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
        handleAction(new MapAction(MapAction.LOGOUT));
    }

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

    private BitmapDescriptor svgToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor findIconForType(String type) {
        if ("Wypadek".equals(type)) {
            return svgToBitmap(R.drawable.ic_wypadek);
        }
        if ("Korek".equals(type)) {
            return svgToBitmap(R.drawable.ic_korek);
        }
        if ("Patrol Policji".equals(type)) {
            return svgToBitmap(R.drawable.ic_radar);
        } else { //Roboty Drogowe
            return svgToBitmap(R.drawable.ic_roboty);
        }
    }

    private void moveCameraToBbox(String bboxStartString, String bboxEndString) {
        LatLng bboxStart = parseCoordinatesString(bboxStartString);
        LatLng bboxEnd = parseCoordinatesString(bboxEndString);

        LatLngBounds bbox = new LatLngBounds(bboxStart, bboxEnd);
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bbox, 0));
    }

    private LatLng parseCoordinatesString(String latLngString) {
        Pattern pattern = Pattern.compile("(.+),(.+)");
        Matcher matcher = pattern.matcher(latLngString.replace("(", "").replace(")", ""));
        if (matcher.find()) {
            return new LatLng(
                    Double.parseDouble(matcher.group(1)),
                    Double.parseDouble(matcher.group(2)));
        }
        return new LatLng(0.0, 0.0);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(), "Button clicked.", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        LatLng currentUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(getApplicationContext(), "onmlc " + currentUserLatLng.latitude + " - " + currentUserLatLng.longitude, Toast.LENGTH_SHORT).show();
        map.moveCamera(CameraUpdateFactory.newLatLng(currentUserLatLng));
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

        for (DrogopolexEvent event : eventListData) {
            addEventToMap(event.getCoordinates(), event.getType());
        }
    }

    private void addEventToMap(LatLng coordinates, String type) {
        map.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(type)
                .icon(findIconForType(type)));
    }
}
