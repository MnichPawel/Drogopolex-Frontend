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
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.LocationResponse;
import com.example.drogopolex.data.network.response.PointsOfInterestResponse;
import com.example.drogopolex.data.network.response.PointsOfInterestValue;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.internal.LinkedTreeMap;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnInfoWindowClickListener {

    GoogleMap map;
    ActivityMapBinding activityMapBinding;

    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    boolean firstLocalizationUpdateLoaded = false;
    boolean displayingSelectedRoute = false;

    private MarkerOptions routeDestinationMarkerOptions = null;
    private Marker routeDestinationMarker = null;
    private List<PointsOfInterestValue> pois = null;

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
        map.setOnMyLocationClickListener(this);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
//        map.setTrafficEnabled(true);


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

        googleMap.setOnInfoWindowClickListener(this);//popup windows
    }

    private void changePositionOfMyLocationButton() { //TODO better location of button
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
    }

    private void prepRequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
            boolean isNearbyEvents = activityMapBinding.getViewModel().onLocationChanged(locationDetails);

            if (!firstLocalizationUpdateLoaded) {//|| isNearbyEvents || !displayingSelectedRoute) {
                firstLocalizationUpdateLoaded = true;
                LatLng location = new LatLng(
                        Double.parseDouble(locationDetails.getLatitude()),
                        Double.parseDouble(locationDetails.getLongitude()));
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
                if(basicResponse.getError() == null) {
                    Toast.makeText(MapActivity.this, "Operacja powiodła się.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, basicResponse.getError(), Toast.LENGTH_SHORT).show();
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
                if (pois != null)
                    pois.forEach(poi -> {
                        LatLng coordinates = parseCoordinatesString(poi.getCoordinates());
                        addPOIToMap(coordinates, poi.getName(), poi.getCategory_name());
                    });
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
            if (result == null) {
                Toast.makeText(MapActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            } else if (result.getError() != null) {
                Toast.makeText(MapActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
            }
            SharedPreferencesUtils.resetSharedPreferences(getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE));
            handleAction(new MapAction(MapAction.LOGOUT));
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

    @Override
    public void onGetPOISuccess(LiveData<PointsOfInterestResponse> pointsOfInterestResponseLiveData) {
        pointsOfInterestResponseLiveData.observe(this, pointsOfInterestResponse -> {
            if (pointsOfInterestResponseLiveData.getValue() != null) {
                if (pois == null) {
                    pois = pointsOfInterestResponseLiveData.getValue().getPois();
                    if (pois != null) {
                        pois.forEach(poi -> {
                            LatLng coordinates = parseCoordinatesString(poi.getCoordinates());
                            addPOIToMap(coordinates, poi.getName(), poi.getCategory_name());
                        });
                    }
                } else {
                    pois = pointsOfInterestResponseLiveData.getValue().getPois();
                }
            }
        });
    }

    @Override
    public void onGetRecommendedRoute(LiveData<RouteValue> routeRec) {
        routeRec.observe(this, routeValue -> {
            if (routeRec.getValue() != null) {
                showAddRuleByNamePopup(routeRec.getValue());
            }
        });
    }

    public void showAddRuleByNamePopup(RouteValue routeValue) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_route_recommendation, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        activityMapBinding.getViewModel().setFirstLoginToFalse();
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.setElevation(20);

        TextView routeToTextView = popupView.findViewById(R.id.placeNameTo);
        routeToTextView.setText("DO: " + routeValue.getTo().getName());

        Button acceptBtn = popupView.findViewById(R.id.accept_rule_by_name_popup_button);
        Button cancelBtn = popupView.findViewById(R.id.cancel_rule_by_name_popup_button);

        popupWindow.showAtLocation(activityMapBinding.getRoot(), Gravity.CENTER, 0, 0);

        acceptBtn.setOnClickListener(v -> {
            activityMapBinding.getViewModel().getRouteFromLocToPoint(routeValue.getTo().getLat(), routeValue.getTo().getLng());
            activityMapBinding.getViewModel().getRouteById(String.valueOf(routeValue.getId()));
            popupWindow.dismiss();
        });
        cancelBtn.setOnClickListener(v -> popupWindow.dismiss());
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
        } else if ("Korek".equals(type)) { //TODO TO POWINNY BYĆ ELSE IFY !!!!!!!!!11!oneone1!
            return svgToBitmap(R.drawable.ic_korek);
        } else if ("Patrol Policji".equals(type)) {
            return svgToBitmap(R.drawable.ic_radar);
        } else if ("Roboty Drogowe".equals(type)) { //Roboty Drogowe
            return svgToBitmap(R.drawable.ic_roboty);
        } else if ("hotel".equals(type)) {
            return svgToBitmap(R.drawable.ic_hotel);
        } else if ("gallery".equals(type)) {
            return svgToBitmap(R.drawable.ic_galeriahandlowa);
        } else if ("library".equals(type)) {
            return svgToBitmap(R.drawable.ic_biblioteka);
        } else if ("museum".equals(type)) {
            return svgToBitmap(R.drawable.ic_galeriasztuki);
        } else if ("college".equals(type)) {
            return svgToBitmap(R.drawable.ic_uczelnia);
        } else if ("kindergarten".equals(type)) {
            return svgToBitmap(R.drawable.ic_uczelnia);
        } else if ("school".equals(type)) {
            return svgToBitmap(R.drawable.ic_uczelnia);
        } else if ("university".equals(type)) {
            return svgToBitmap(R.drawable.ic_uczelnia);
        } else if ("bank".equals(type)) {
            return svgToBitmap(R.drawable.ic_bank);
        } else if ("dentist".equals(type)) {
            return svgToBitmap(R.drawable.ic_dentysta);
        } else if ("hospital".equals(type)) {
            return svgToBitmap(R.drawable.ic_szpital);
        } else if ("pharmacy".equals(type)) {
            return svgToBitmap(R.drawable.ic_szpital); //TODO: ikonka dla apteki chyba
        } else if ("fitness_centre".equals(type)) {
            return svgToBitmap(R.drawable.ic_sport);
        } else if ("swimming_pool".equals(type)) {
            return svgToBitmap(R.drawable.ic_sport);
        } else if ("stadium".equals(type)) {
            return svgToBitmap(R.drawable.ic_sport);
        } else if ("cinema".equals(type)) {
            return svgToBitmap(R.drawable.ic_galeriasztuki); //TODO: ikonka dla kina (must have)
        } else if ("park".equals(type)) {
            return svgToBitmap(R.drawable.ic_park);
        } else if ("zoo".equals(type)) {
            return svgToBitmap(R.drawable.ic_park); //TODO: ikonka dla zoo chyba
        } else if ("fire_station".equals(type)) {
            return svgToBitmap(R.drawable.ic_hotel); //TODO: ikonka dla strazy pozarnej (must have)
        } else if ("police".equals(type)) {
            return svgToBitmap(R.drawable.ic_hotel); //TODO: ikonka dla policji (must have)
        } else if ("post_office".equals(type)) {
            return svgToBitmap(R.drawable.ic_poczta);
        } else if ("townhall".equals(type)) {
            return svgToBitmap(R.drawable.ic_hotel); //TODO: ikonka dla ratusza
        } else if ("hairdresser".equals(type)) {
            return svgToBitmap(R.drawable.ic_hotel); //TODO: ikonak dla fryzjera
        } else if ("bar".equals(type)) {
            return svgToBitmap(R.drawable.ic_pub);
        } else if ("fast_food".equals(type)) {
            return svgToBitmap(R.drawable.ic_jedzenie);
        } else if ("pub".equals(type)) {
            return svgToBitmap(R.drawable.ic_pub);
        } else if ("restaurant".equals(type)) {
            return svgToBitmap(R.drawable.ic_jedzenie);
        } else if ("fuel".equals(type)) {
            return svgToBitmap(R.drawable.ic_paliwo);
        } else if ("parking".equals(type)) {
            return svgToBitmap(R.drawable.ic_parking);
        } else if ("railway_station".equals(type)) {
            return svgToBitmap(R.drawable.ic_ciapolongi);
        } else if ("public_transport_station".equals(type)) {
            return svgToBitmap(R.drawable.ic_busy);
        } else {
            return svgToBitmap(R.drawable.ic_fontanna); // fountain
        }
    }

    private void moveCameraToBbox(LocationResponse bboxStartResponse, LocationResponse bboxEndResponse) {
        LatLng bboxStart = new LatLng(bboxStartResponse.getLat(), bboxStartResponse.getLng());
        LatLng bboxEnd = new LatLng(bboxEndResponse.getLat(), bboxEndResponse.getLng());

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

        if (pois != null) {
            pois.forEach(poi -> {
                LatLng coordinates = parseCoordinatesString(poi.getCoordinates());
                addPOIToMap(coordinates, poi.getName(), poi.getCategory_name());
            });
        }

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

    private void addPOIToMap(LatLng coordinates, String name, String type) {
        map.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(name)
                .icon(findIconForType(type)));
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        //TODO to implement
    }
}
