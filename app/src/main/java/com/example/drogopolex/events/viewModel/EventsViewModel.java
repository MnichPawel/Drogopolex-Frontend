package com.example.drogopolex.events.viewModel;

import android.app.Application;

import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.services.LocationLiveData;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EventsViewModel extends AndroidViewModel {
    private MutableLiveData<EventsAction> mAction = new MutableLiveData<>();
    private LiveData<EventsResponse> eventsLiveData = new MutableLiveData<>();
    private LocationLiveData locationLiveData;

    public OnSuccessListener<LiveData<EventsResponse>> onSuccessListener = null;

    private EventsRepository eventsRepository;

    public EventsViewModel(Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        eventsRepository = new EventsRepository();
    }

    public void onLocationChanged(LocationDetails location) {
        eventsLiveData = eventsRepository.getEventsFromUserArea(location.getLatitude(), location.getLongitude());
        onSuccessListener.onSuccess(eventsLiveData);
    }

    public void onReturnClicked() {
        mAction.setValue(new EventsAction(EventsAction.SHOW_LOGGED_IN));
    }

    public void onSearchClicked() {
        mAction.setValue(new EventsAction(EventsAction.SHOW_EVENTS_SEARCH));
    }

    public LocationLiveData getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<EventsAction> getAction() {
        return mAction;
    }
}
