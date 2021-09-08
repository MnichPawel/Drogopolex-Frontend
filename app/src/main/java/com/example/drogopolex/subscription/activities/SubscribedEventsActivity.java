package com.example.drogopolex.subscription.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.adapters.EventListAdapter;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.databinding.ActivitySubscribedEventsBinding;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.VoteType;
import com.example.drogopolex.subscription.utils.SubscribedEventsAction;
import com.example.drogopolex.subscription.viewModel.SubscribedEventsViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribedEventsActivity extends AppCompatActivity implements SharedPreferencesHolder, OnSuccessListener<LiveData<EventsResponse>> {
    RecyclerView recyclerView;
    ActivitySubscribedEventsBinding activitySubscribedEventsBinding;
    EventListAdapter eventListAdapter;
    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySubscribedEventsBinding = DataBindingUtil.setContentView(this, R.layout.activity_subscribed_events);
        activitySubscribedEventsBinding.setViewModel(new SubscribedEventsViewModel(getApplication()));
        activitySubscribedEventsBinding.executePendingBindings();
        activitySubscribedEventsBinding.getViewModel().sharedPreferencesHolder = this;
        activitySubscribedEventsBinding.getViewModel().onSuccessListener = this;

        recyclerView = (RecyclerView) findViewById(R.id.subscribed_events_view);
        activitySubscribedEventsBinding.getViewModel().getAction().observe(this, new Observer<SubscribedEventsAction>() {
        @Override
        public void onChanged(SubscribedEventsAction subscriptionsEventAction) {
            if(subscriptionsEventAction != null){
                handleAction(subscriptionsEventAction);
            }
        }
         });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }

        prepRequestSubscribedEvents();
    }

    private void handleAction(SubscribedEventsAction subscribedEventsAction) {
        switch (subscribedEventsAction.getValue()) {
            case SubscribedEventsAction.ADD_SUBSCRIPTION:
                Intent goToAddSubIntent = new Intent(this, SubscribeActivity.class);
                startActivity(goToAddSubIntent);
                break;
            case SubscribedEventsAction.SHOW_SUBSCRIPTIONS:
                Intent goToSubscriptionsIntent = new Intent(this, SubscriptionsActivity.class);
                startActivity(goToSubscriptionsIntent);
                break;
            case SubscribedEventsAction.GO_TO_MAIN_MENU:
                Intent goToMainMenuIntent = new Intent(this, LoggedInMenuActivity.class);
                startActivity(goToMainMenuIntent);
        }
    }

    private void prepRequestSubscribedEvents() {
        activitySubscribedEventsBinding.getViewModel().requestSubscribedEvents();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
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
                                VoteType userVoteType;
                                if("1".equals(event.getUserVote())) userVoteType = VoteType.UPVOTED;
                                else if("-1".equals(event.getUserVote())) userVoteType = VoteType.DOWNVOTED;
                                else userVoteType = VoteType.NO_VOTE;

                                eventListData.add(new DrogopolexEvent(
                                        event.getType(),
                                        event.getCountry(),
                                        event.getStreet(),
                                        Integer.parseInt(event.getId()),
                                        Integer.parseInt(event.getValueOfVotes()),
                                        userVoteType
                                ));
                            });
                    if(eventListAdapter != null) {
                        eventListAdapter.notifyDataSetChanged();
                    } else {
                        eventListAdapter = new EventListAdapter(eventListData, SubscribedEventsActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(SubscribedEventsActivity.this));
                        recyclerView.setAdapter(eventListAdapter);
                    }
                } else {
                    Toast.makeText(SubscribedEventsActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}