package com.example.drogopolex.events.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.databinding.ActivityMapBinding;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.events.viewModel.MapViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.model.VoteType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static com.example.drogopolex.constants.AppConstant.PERMISSIONS_REQUEST_LOCATION;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, OnSuccessListener<LiveData<EventsResponse>>, SharedPreferencesHolder {

    GoogleMap map;
    ActivityMapBinding activityMapBinding;

    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        activityMapBinding.setViewModel(new MapViewModel(getApplication()));
        activityMapBinding.executePendingBindings();
        activityMapBinding.getViewModel().onSuccessListener = this;
        activityMapBinding.getViewModel().sharedPreferencesHolder = this;

        activityMapBinding.getViewModel().getAction().observe(this, new Observer<EventsAction>() {
            @Override
            public void onChanged(EventsAction eventsAction) {
                if(eventsAction != null){
                    handleAction(eventsAction);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    private void handleAction(EventsAction eventsAction) {
        switch (eventsAction.getValue()) {
            case EventsAction.SHOW_LOGGED_IN:
                Intent goToLoggedInIntent = new Intent(this, LoggedInMenuActivity.class);
                startActivity(goToLoggedInIntent);
                break;
            case EventsAction.SHOW_EVENTS_SEARCH:
                Intent goToEventsSearchIntent = new Intent(this, EventsSearchActivity.class);
                startActivity(goToEventsSearchIntent);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        prepRequestLocationUpdates();
    }

    private void prepRequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSIONS_REQUEST_LOCATION);
        } else {
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        activityMapBinding.getViewModel().getLocationLiveData().observe(this, new Observer<LocationDetails>() {
            @Override
            public void onChanged(LocationDetails locationDetails) {
                activityMapBinding.getViewModel().onLocationChanged(locationDetails);
                LatLng location = new LatLng(
                        Double.parseDouble(locationDetails.getLatitude()),
                        Double.parseDouble(locationDetails.getLatitude()));
                map.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
        });
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }

    @Override
    public void onSuccess(LiveData<EventsResponse> eventsResponseLiveData) {
        eventsResponseLiveData.observe(this, new Observer<EventsResponse>() {
            @Override
            public void onChanged(EventsResponse eventsResponse) {
                if(eventsResponse != null) {
                    eventListData.clear();
                    eventsResponse.getEvents()
                            .forEach(event -> {
                                VoteType userVoteType;
                                if("1".equals(event.getUserVote())) userVoteType = VoteType.UPVOTED;
                                else if("-1".equals(event.getUserVote())) userVoteType = VoteType.DOWNVOTED;
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

                                map.addMarker(new MarkerOptions().position(coordinates).title(event.getType()));
                            });

                } else {
                    Toast.makeText(MapActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addMapMarker(String name, LatLng latLng) {
        LatLng maharashtra = new LatLng(19.204847, 73.039494);
        map.addMarker(new MarkerOptions().position(maharashtra).title("Maharashtra"));
        map.moveCamera(CameraUpdateFactory.newLatLng(maharashtra));
    }

    private LatLng parseCoordinatesString(String latLngString) {
        Pattern pattern = Pattern.compile("(.+),(.+)");
        Matcher matcher = pattern.matcher(latLngString.replace("(","").replace(")",""));
        if(matcher.find()){
            return new LatLng(
                    Double.parseDouble(matcher.group(1)),
                    Double.parseDouble(matcher.group(2)));
        }
        return new LatLng(0.0,0.0); //TODO exception
    }
}
