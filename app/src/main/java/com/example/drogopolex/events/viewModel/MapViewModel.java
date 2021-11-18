package com.example.drogopolex.events.viewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.drogopolex.R;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.events.listeners.MapActivityListener;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.services.LocationLiveData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.databinding.PropertyChangeRegistry;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MapViewModel extends AndroidViewModel implements Observable {
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private MutableLiveData<EventsAction> mAction = new MutableLiveData<>();
    private LiveData<EventsResponse> eventsLiveData = new MutableLiveData<>();
    private LocationLiveData locationLiveData;

    public MapActivityListener mapActivityListener = null;
    public SharedPreferencesHolder sharedPreferencesHolder = null;

    private final EventsRepository eventsRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    public boolean addEventButtonClicked = false;
    private boolean isOnlySubs = false;
    public ObservableField<Boolean> menuOpened = new ObservableField<>(false);

    private final Animation flipButtonOut;

    public MapViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        eventsRepository = new EventsRepository();
        subscriptionsRepository = new SubscriptionsRepository();

        flipButtonOut = AnimationUtils.loadAnimation(application.getApplicationContext(), R.anim.flip_button_out);
    }

    public LiveData<EventsAction> getAction() {
        return mAction;
    }

    public LocationLiveData getLocationLiveData() {
        return locationLiveData;
    }

    public boolean onLocationChanged(LocationDetails location) {
        if (!isOnlySubs) {
            fetchNearbyEvents(location);
            return true;
        }
        return false;
    }

    public void onAddEventClicked() {
        Log.d("MAP_BINDING_DEBUG", "onAddEventClicked");
        addEventButtonClicked = !addEventButtonClicked;
        notifyPropertyChanged(BR.addEventButtonClicked);
    }

    public void onEventWypadekClicked() {
        addNewEvent("Wypadek");
    }

    public void onEventKorekClicked() {
        addNewEvent("Korek");
    }

    public void onEventPatrolPolicjiClicked() {
        addNewEvent("Patrol Policji");
    }

    public void onEventRobotyDrogoweClicked() {
        addNewEvent("Roboty Drogowe");
    }

    public void onMenuClicked() {


        menuOpened.set(!menuOpened.get());
        if(menuOpened.get()){
            //ButtonsAnimationBinding.setVisibilityMenu(o tutaj chce sobie napisać ktory guzik,menuOpened.get());

        }
    }

    public void onLogoutClicked() {

        //        to implement
    }

    public void onGoToSubsClicked() {
        //        to implement
    }

    public void onGoToProfileClicked() {
        //        to implement
    }

    public void onShowAllEventsClicked(View view) {
        Log.d("SHOW_ALL_EVENTS", "saec  " + view.getVisibility());
        view.startAnimation(flipButtonOut);
        if (isOnlySubs) { // switch to nearby events
            isOnlySubs = false;
            ((FloatingActionButton) view).setImageResource(R.drawable.ic_switch_left);
            fetchNearbyEvents();
        } else {  // switch to subscribed events
            isOnlySubs = true;
            ((FloatingActionButton) view).setImageResource(R.drawable.ic_switch_right);
            fetchSubscribedEvents();
        }
    }

    @Bindable
    public boolean isAddEventButtonClicked() {
        return addEventButtonClicked;
    }

    @Override
    public void addOnPropertyChangedCallback(
            Observable.OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(
            Observable.OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }

    private void fetchSubscribedEvents() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");
        eventsLiveData = subscriptionsRepository.getSubscribedEvents(token, userId);
        mapActivityListener.onGetEventsSuccess(eventsLiveData);
    }

    private void fetchNearbyEvents() {
        LocationDetails locationDetails = locationLiveData.getValue();
        if (locationDetails != null) {
            fetchNearbyEvents(locationDetails);
        }
    }

    private void fetchNearbyEvents(LocationDetails locationDetails) {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");
        eventsLiveData = eventsRepository.getEventsFromUserArea(userId, token, locationDetails.getLatitude(), locationDetails.getLongitude());
        mapActivityListener.onGetEventsSuccess(eventsLiveData);
    }

    private void addNewEvent(String type) {
        LocationDetails locationDetails = locationLiveData.getValue();
        if (locationDetails != null) {
            SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
            String userId = sharedPreferences.getString("user_id", "");
            String token = sharedPreferences.getString("token", "");

            LiveData<BasicResponse> addEventResponse = eventsRepository.addEventByGps(locationDetails, type, userId, token);
            mapActivityListener.onAddNewEventSuccess(addEventResponse);
        }
    }
}
