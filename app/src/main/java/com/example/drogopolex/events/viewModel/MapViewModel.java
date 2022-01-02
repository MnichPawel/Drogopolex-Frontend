package com.example.drogopolex.events.viewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.drogopolex.R;
import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.request.GenerateRouteRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.PointsOfInterestResponse;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.data.repositories.RecommendationRepository;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.data.repositories.UserRepository;
import com.example.drogopolex.events.listeners.MapActivityListener;
import com.example.drogopolex.events.utils.MapAction;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.services.LocationLiveData;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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
    private static boolean isFirstLogin = true;
    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
    private final MutableLiveData<MapAction> mAction = new MutableLiveData<>();
    private final LocationLiveData locationLiveData;
    private final EventsRepository eventsRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;
    private final Animation flipButtonOut;
    public MapActivityListener mapActivityListener = null;
    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public boolean addEventButtonClicked = false;
    public ObservableField<Boolean> addQuickRouteClicked = new ObservableField<>(false);
    public ObservableField<Boolean> confirmQuickRouteClicked = new ObservableField<>(false);
    public ObservableField<Boolean> menuOpened = new ObservableField<>(false);
    private LiveData<EventsResponse> eventsLiveData = new MutableLiveData<>();
    private boolean isOnlySubs = false;
    private boolean isChoosePointMode = false;

    public MapViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        eventsRepository = new EventsRepository();
        subscriptionsRepository = new SubscriptionsRepository();
        userRepository = new UserRepository();
        recommendationRepository = new RecommendationRepository();

        flipButtonOut = AnimationUtils.loadAnimation(application.getApplicationContext(), R.anim.flip_button_out);
    }

    public LiveData<MapAction> getAction() {
        return mAction;
    }

    public LocationLiveData getLocationLiveData() {
        return locationLiveData;
    }

    public void setFirstLoginToFalse() {
        isFirstLogin = false;
    }

    public boolean onLocationChanged(LatLngBounds latLngBounds) {
        LocationDetails location = locationLiveData.getValue();
        if (location != null) {
            SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
            String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
            String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

            if (isFirstLogin) {
                LiveData<RouteValue> routeRec = recommendationRepository.getRecommendedRoute(userId, token);
                mapActivityListener.recommendRoute(routeRec);
            }

            LiveData<PointsOfInterestResponse> poiLiveData = recommendationRepository.getPointsFromUserArea(
                    userId,
                    token,
                    location.getLatitude(),
                    location.getLongitude()
            );
            mapActivityListener.onGetPOISuccess(poiLiveData);
        }
        if (!isOnlySubs) {
            fetchNearbyEvents(latLngBounds);
        }
        return !(isOnlySubs || isChoosePointMode);
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
        Log.d("MAP_LAYOUT_DEBUG", "onEventKorekClicked");
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
    }

    public void onLogoutClicked() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        boolean loggedIn = sharedPreferences.getBoolean("loggedIn", false);
        String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        if (!loggedIn) {
            mapActivityListener.onLogoutFailure("Użytkownik nie jest zalogowany");
        }

        LiveData<BasicResponse> response = userRepository.userLogout(userId, token);
        mapActivityListener.onLogoutSuccess(response);
    }

    public void onGoToSubsClicked() {
        mAction.setValue(new MapAction(MapAction.SHOW_SUBSCRIPTIONS));
    }

    public void onGoToProfileClicked() {
        mAction.setValue(new MapAction(MapAction.SHOW_PROFILE));
    }

    public void onGoToRoutesListClicked() {
        mAction.setValue(new MapAction(MapAction.SHOW_ROUTES_LIST));
    }

    public void onShowAllEventsClicked(View view) {
        Log.d("SHOW_ALL_EVENTS", "saec  " + view.getVisibility());
        view.startAnimation(flipButtonOut);
        if (isOnlySubs) { // switch to nearby events
            isOnlySubs = false;
            ((ImageView) view).setImageResource(R.drawable.ic_switch_left);
            LatLngBounds latLngBounds = mapActivityListener.getMapBounds();
            fetchNearbyEvents(latLngBounds);
        } else {  // switch to subscribed events
            isOnlySubs = true;
            ((ImageView) view).setImageResource(R.drawable.ic_switch_right);
            fetchSubscribedEvents();
        }
    }

    public void onQuickNewRouteClicked() {
        if (Boolean.TRUE.equals(confirmQuickRouteClicked.get())) { //if we are already showing a route, don't make another, hide existing one instead
            onQuitQuickRouteClicked();
        } else {
            addQuickRouteClicked.set(!addQuickRouteClicked.get());
            LocationDetails locationDetails = locationLiveData.getValue();
            if (locationDetails != null) {
                isChoosePointMode = true;
                mapActivityListener.onChoosePointModeEntered(new LatLng(
                        Double.parseDouble(locationDetails.getLatitude()),
                        Double.parseDouble(locationDetails.getLongitude())));
            }
        }
    }

    public void onConfirmQuickRouteClicked() {
        addQuickRouteClicked.set(!addQuickRouteClicked.get());
        LatLng chosenPoint = mapActivityListener.getChosenPoint();
        LocationDetails userLocation = locationLiveData.getValue();

        if (userLocation != null) {
            SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
            String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
            String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

            LatLng from = new LatLng(Double.parseDouble(userLocation.getLatitude()), Double.parseDouble(userLocation.getLongitude()));
            LiveData<RouteValue> routeResponseLiveData = eventsRepository.generateRoute(new GenerateRouteRequest(from, chosenPoint, null, null), userId, token);

            confirmQuickRouteClicked.set(!confirmQuickRouteClicked.get());
            mapActivityListener.drawRoute(routeResponseLiveData);
        }
    }

    public void onQuitQuickRouteClicked() {
        confirmQuickRouteClicked.set(!confirmQuickRouteClicked.get());
        mAction.setValue(new MapAction(MapAction.RESET_ROUTE));
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
        String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");
        eventsLiveData = subscriptionsRepository.getSubscribedEvents(token, userId);
        mapActivityListener.onGetEventsSuccess(eventsLiveData);
    }

    private void fetchNearbyEvents(LatLngBounds latLngBounds) {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");
        eventsLiveData = eventsRepository.getEventsFromUserArea(
                userId,
                token,
                latLngBounds
        );
        mapActivityListener.onGetEventsSuccess(eventsLiveData);
    }

    private void addNewEvent(String type) {
        LocationDetails locationDetails = locationLiveData.getValue();
        if (locationDetails != null) {
            SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
            String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
            String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

            LiveData<BasicResponse> addEventResponse = eventsRepository.addEventByGps(locationDetails, type, userId, token);
            mapActivityListener.onAddNewEventSuccess(addEventResponse);
        }
    }

    public void getRouteById(String routeId) {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        LiveData<RouteValue> routeResponseLiveData = eventsRepository.getRoute(userId, token, routeId);
        confirmQuickRouteClicked.set(!confirmQuickRouteClicked.get());
        mapActivityListener.drawRoute(routeResponseLiveData);
    }

    public void getRouteFromLocToPoint(Double latitude, Double longitude) {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        LocationDetails locationDetails = locationLiveData.getValue();
        if (locationDetails != null) {
            LatLng fromPoint = new LatLng(Double.parseDouble(locationDetails.getLatitude()), Double.parseDouble(locationDetails.getLongitude()));
            LatLng toPoint = new LatLng(latitude, longitude);
            GenerateRouteRequest generateRouteRequest = new GenerateRouteRequest(fromPoint, toPoint, null, null);

            LiveData<RouteValue> routeValueLiveData = eventsRepository.generateRoute(generateRouteRequest, userId, token);
            confirmQuickRouteClicked.set(!confirmQuickRouteClicked.get());
            mapActivityListener.drawRoute(routeValueLiveData);
        }
    }
}
