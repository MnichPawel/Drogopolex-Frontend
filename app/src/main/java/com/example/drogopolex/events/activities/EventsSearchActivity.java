package com.example.drogopolex.events.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.constants.EventTypes;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.databinding.ActivityEventsSearchBinding;
import com.example.drogopolex.events.listeners.EventsListener;
import com.example.drogopolex.events.listeners.SpinnerHolder;
import com.example.drogopolex.events.utils.EventsSearchAction;
import com.example.drogopolex.events.viewModel.EventsSearchViewModel;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.Vote;
import com.example.drogopolex.model.VoteType;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

public class EventsSearchActivity extends AppCompatActivity implements EventsListener, SpinnerHolder, SharedPreferencesHolder {
    ActivityEventsSearchBinding activityEventsSearchBinding;

    RecyclerView recyclerView;

    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEventsSearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_events_search);
        activityEventsSearchBinding.setViewModel(new EventsSearchViewModel());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, EventTypes.getEventTypes());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        activityEventsSearchBinding.setSpinnerAdapter(spinnerArrayAdapter);
        activityEventsSearchBinding.executePendingBindings();
        activityEventsSearchBinding.getViewModel().eventsListener = this;
        activityEventsSearchBinding.getViewModel().spinnerHolder = this;
        activityEventsSearchBinding.getViewModel().sharedPreferencesHolder = this;

        activityEventsSearchBinding.getViewModel().getAction().observe(this, new Observer<EventsSearchAction>() {
            @Override
            public void onChanged(EventsSearchAction eventsSearchAction) {
                if(eventsSearchAction != null){
                    handleAction(eventsSearchAction);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.eventsSearchListView);

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)) {
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }
    }

    private void handleAction(EventsSearchAction action) {
        if (action.getValue() == EventsSearchAction.SHOW_LOGGED_IN) {
            Intent goToLoggedInIntent = new Intent(this, LoggedInMenuActivity.class);
            startActivity(goToLoggedInIntent);
        }
    }


    @Override
    public String getSelectedItem() {
        return (String) activityEventsSearchBinding.spinnerEventSearch.getSelectedItem();
    }

    @Override
    public void onSuccess(LiveData<EventsResponse> response) {
        response.observe(this, new Observer<EventsResponse>() {
            @Override
            public void onChanged(EventsResponse eventsResponse) {
                if(eventsResponse != null) {
                    eventListData.clear();
                    eventsResponse.getEvents()
                            .forEach(event -> {
                                eventListData.add(new DrogopolexEvent(
                                        event.getType(),
                                        event.getCountry(),
                                        event.getStreet(),
                                        Integer.parseInt(event.getId()),
                                        new ArrayList<Vote>(),
                                        VoteType.UPVOTED
                                ));
                            });
                    activityEventsSearchBinding.getViewModel().setEventsInAdapter(eventListData);
                } else {
                    Toast.makeText(EventsSearchActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }
}
