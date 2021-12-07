package com.example.drogopolex.events.viewModel;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.drogopolex.data.network.request.GenerateRouteRequest;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.AddRouteActivityListener;
import com.example.drogopolex.events.utils.AddRouteAction;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.services.LocationLiveData;
import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AddRouteViewModel extends AndroidViewModel {
    private MutableLiveData<AddRouteAction> mAction = new MutableLiveData<>();

    public MutableLiveData<String> sourcePlace = new MutableLiveData<>();
    public MutableLiveData<String> destinationPlace = new MutableLiveData<>();
    public MutableLiveData<String> nameOfRoute = new MutableLiveData<>();

    private LocationLiveData locationLiveData;

    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public AddRouteActivityListener addRouteActivityListener = null;

    private final EventsRepository eventsRepository;

    public AddRouteViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        eventsRepository = new EventsRepository();
    }

    public void onReturnClicked() {
        mAction.setValue(new AddRouteAction(AddRouteAction.SHOW_ROUTES_LIST));
    }

    public void onAddRouteClicked() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String user_id = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");

        LatLng sourceLatLng = addRouteActivityListener.getLatLngOfChosenPoint(true);
        LatLng destinationLatLng = addRouteActivityListener.getLatLngOfChosenPoint(false);

        String name = nameOfRoute.getValue();
        String source = sourcePlace.getValue();
        String dest = destinationPlace.getValue();

        LiveData<RouteValue> routeResponseLiveData;
        if (!isLatLngActive(sourceLatLng, source) && !isLatLngActive(destinationLatLng, dest)) {
            routeResponseLiveData =
                    eventsRepository.generateRoute(
                            new GenerateRouteRequest(name, source, dest),
                            user_id, token);

        } else if (isLatLngActive(sourceLatLng, source) && !isLatLngActive(destinationLatLng, dest)) {
            routeResponseLiveData =
                    eventsRepository.generateRoute(
                            new GenerateRouteRequest(name, sourceLatLng, dest),
                            user_id, token);

        } else if (!isLatLngActive(sourceLatLng, source) && isLatLngActive(destinationLatLng, dest)) {
            routeResponseLiveData =
                    eventsRepository.generateRoute(
                            new GenerateRouteRequest(name, source, destinationLatLng),
                            user_id, token);

        } else {
            routeResponseLiveData =
                    eventsRepository.generateRoute(
                            new GenerateRouteRequest(name, sourceLatLng, destinationLatLng),
                            user_id, token);
        }

        addRouteActivityListener.onSuccessAddRoute(routeResponseLiveData);
    }

    private boolean isLatLngActive(LatLng latLng, String stringLocation) {
        return latLng != null && stringLocation.equals(latLng.toString());
    }

    public void onSourceByMapClicked() {
        mAction.setValue(new AddRouteAction(AddRouteAction.CHOOSE_SOURCE_POINT));
    }

    public void onDestinationByMapClicked() {
        mAction.setValue(new AddRouteAction(AddRouteAction.CHOOSE_DESTINATION_POINT));
    }

    public void onAddRuleClicked() {
        mAction.setValue(new AddRouteAction(AddRouteAction.CHOOSE_SOURCE_POINT));
    }

    public LiveData<AddRouteAction> getAction() {
        return mAction;
    }

    public LocationLiveData getLocationLiveData() {
        return locationLiveData;
    }
}
