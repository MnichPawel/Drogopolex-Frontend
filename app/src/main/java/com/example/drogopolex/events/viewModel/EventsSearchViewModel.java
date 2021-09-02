package com.example.drogopolex.events.viewModel;

import com.example.drogopolex.adapters.EventListAdapter;

import android.content.SharedPreferences;

import com.example.drogopolex.constants.EventTypes;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.SpinnerHolder;
import com.example.drogopolex.events.utils.EventsSearchAction;

import com.example.drogopolex.model.DrogopolexEvent;

import java.util.ArrayList;

import com.example.drogopolex.listeners.SharedPreferencesHolder;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EventsSearchViewModel extends EventListViewModel {
    private MutableLiveData<EventsSearchAction> mAction = new MutableLiveData<>();
    public MutableLiveData<String> localization = new MutableLiveData<>();

    public SpinnerHolder spinnerHolder = null;
    public SharedPreferencesHolder sharedPreferencesHolder = null;

    public EventsSearchViewModel() {
        this.eventsRepository = new EventsRepository();
        this.adapter = new EventListAdapter(this);
    }

    public void setEventsInAdapter(ArrayList<DrogopolexEvent> events) {
        adapter.setLocalDataSet(events);
        adapter.notifyDataSetChanged();
    }

    public void onReturnClicked() {
        mAction.setValue(new EventsSearchAction(EventsSearchAction.SHOW_LOGGED_IN));
    }

    public void onSearchClicked() {
        String localizationValue = localization.getValue();
        String selectedEventType = spinnerHolder.getSelectedItem();

        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");

        if(localizationValue == null || selectedEventType == null ||
                (localizationValue.isEmpty() && (selectedEventType.isEmpty() ||
                selectedEventType.equals(EventTypes.getEventTypes().get(0))))) {
            eventsListener.onFailure("Nieprawidłowa lokalizacja lub typ zdarzenia.");
        } else if(selectedEventType.isEmpty() || selectedEventType.equals(EventTypes.getEventTypes().get(0))) {
            LiveData<EventsResponse> getEventsByLocalization = eventsRepository.getEventsByLocalization(userId, token, localizationValue);
            eventsListener.onSuccess(getEventsByLocalization);
        } else if(localizationValue.isEmpty()) {
            LiveData<EventsResponse> getEventsByType = eventsRepository.getEventsByType(userId, token, selectedEventType);
            eventsListener.onSuccess(getEventsByType);
        } else {
            LiveData<EventsResponse> getEventsByTypeAndLocalization = eventsRepository.getEventsByTypeAndLocalization(userId, token, selectedEventType, localizationValue);
            eventsListener.onSuccess(getEventsByTypeAndLocalization);
        }
    }

    public LiveData<EventsSearchAction> getAction() {
        return mAction;
    }


}
