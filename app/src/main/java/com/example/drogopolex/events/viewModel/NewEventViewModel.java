package com.example.drogopolex.events.viewModel;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.drogopolex.constants.EventTypes;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.SpinerHolder;
import com.example.drogopolex.events.utils.NewEventAction;
import com.example.drogopolex.listeners.BasicListener;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.services.LocationLiveData;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NewEventViewModel extends AndroidViewModel {
    private MutableLiveData<NewEventAction> mAction = new MutableLiveData<>();
    public MutableLiveData<String> localization = new MutableLiveData<>();

    private LocationLiveData locationLiveData;

    public BasicListener basicListener = null;
    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public SpinerHolder spinnerHolder = null;

    private EventsRepository eventsRepository;

    public NewEventViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        eventsRepository = new EventsRepository();
    }

    public void onReturnClicked() {
        mAction.setValue(new NewEventAction(NewEventAction.SHOW_LOGGED_IN));
    }

    public void onAddNewEventClicked() {
        String localizationValue = localization.getValue();
        String selectedEventType = spinnerHolder.getSelectedItem();

        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");

        if(localizationValue == null || localizationValue.isEmpty() ||
                selectedEventType == null || selectedEventType.isEmpty() ||
                selectedEventType.equals(EventTypes.getEventTypes().get(0))) {
            basicListener.onFailure("Nieprawidłowa lokalizacja lub typ zdarzenia.");
        } else {
            LiveData<BasicResponse> addEventResponse = eventsRepository.addEvent(localizationValue, selectedEventType, userId, token);
            basicListener.onSuccess(addEventResponse);
        }
    }

    public void onAddNewEventGpsClicked() {
        mAction.setValue(new NewEventAction(NewEventAction.ADD_EVENT_BY_GPS));
    }

    public void onAddNewEventGpsReady(LocationDetails locationDetails) {
        String selectedEventType = spinnerHolder.getSelectedItem();

        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");

        if(selectedEventType == null || selectedEventType.isEmpty() ||
                selectedEventType.equals(EventTypes.getEventTypes().get(0))) {
            basicListener.onFailure("Nieprawidłowy typ zdarzenia.");
        } else {
            LiveData<BasicResponse> addEventResponse = eventsRepository.addEventByGps(locationDetails, selectedEventType, userId, token);
            basicListener.onSuccess(addEventResponse);
        }
    }

    public LocationLiveData getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<NewEventAction> getAction() {
        return mAction;
    }
}
