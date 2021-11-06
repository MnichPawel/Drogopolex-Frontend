package com.example.drogopolex.events.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.adapters.EventListAdapter;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.databinding.ActivityEventsBinding;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.events.viewModel.EventsViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.model.VoteType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.drogopolex.constants.AppConstant.PERMISSIONS_REQUEST_LOCATION;

public class EventsActivity extends AppCompatActivity implements OnSuccessListener<LiveData<EventsResponse>>, SharedPreferencesHolder {
    ActivityEventsBinding activityEventsBinding;
    EventListAdapter eventListAdapter;

    RecyclerView recyclerView;

    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEventsBinding = DataBindingUtil.setContentView(this, R.layout.activity_events);
        activityEventsBinding.setViewModel(new EventsViewModel(getApplication()));
        activityEventsBinding.executePendingBindings();
        activityEventsBinding.getViewModel().onSuccessListener = this;
        activityEventsBinding.getViewModel().sharedPreferencesHolder = this;

        activityEventsBinding.getViewModel().getAction().observe(this, new Observer<EventsAction>() {
            @Override
            public void onChanged(EventsAction eventsAction) {
                if(eventsAction != null){
                    handleAction(eventsAction);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.eventsListView);

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }

        prepRequestLocationUpdates();
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
        activityEventsBinding.getViewModel().getLocationLiveData().observe(this, new Observer<LocationDetails>() {
            @Override
            public void onChanged(LocationDetails locationDetails) {
                activityEventsBinding.getViewModel().onLocationChanged(locationDetails);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Nie można uzyskać lokalizacji bez uprawnień.", Toast.LENGTH_SHORT).show();
            }
        }
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

                                eventListData.add(new DrogopolexEvent(
                                        event.getType(),
                                        event.getCountry(),
                                        event.getStreet(),
                                        Integer.parseInt(event.getId()),
                                        new LatLng(0.0,0.0),
                                        Integer.parseInt(event.getValueOfVotes()),
                                        userVoteType
                                ));
                            });
                    if(eventListAdapter != null) {
                        eventListAdapter.notifyDataSetChanged();
                    } else {
                        eventListAdapter = new EventListAdapter(eventListData, EventsActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(EventsActivity.this));
                        recyclerView.setAdapter(eventListAdapter);
                    }
                } else {
                    Toast.makeText(EventsActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }
}
