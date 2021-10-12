package com.example.drogopolex.events.viewModel;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.services.LocationLiveData;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MapViewModel extends AndroidViewModel {
    private MutableLiveData<EventsAction> mAction = new MutableLiveData<>();
    private LiveData<EventsResponse> eventsLiveData = new MutableLiveData<>();
    private LocationLiveData locationLiveData;

    public OnSuccessListener<LiveData<EventsResponse>> onSuccessListener = null;
    public SharedPreferencesHolder sharedPreferencesHolder = null;

    private EventsRepository eventsRepository;

    public MapViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        eventsRepository = new EventsRepository();
    }

    public LiveData<EventsAction> getAction() {
        return mAction;
    }

    public LocationLiveData getLocationLiveData() {
        return locationLiveData;
    }

    public void onLocationChanged(LocationDetails location) {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");
        eventsLiveData = eventsRepository.getEventsFromUserArea(userId, token, location.getLatitude(), location.getLongitude());
        onSuccessListener.onSuccess(eventsLiveData);
    }
}
