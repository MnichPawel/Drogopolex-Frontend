package com.example.drogopolex.events.viewModel;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.request.GenerateRouteRequest;
import com.example.drogopolex.data.network.request.LocationRequest;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.AddRouteActivityListener;
import com.example.drogopolex.events.utils.AddRouteAction;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.rules.DrogopolexEventTypeRule;
import com.example.drogopolex.model.rules.DrogopolexNameRule;
import com.example.drogopolex.model.rules.DrogopolexPointRule;
import com.example.drogopolex.model.rules.DrogopolexRule;
import com.example.drogopolex.services.LocationLiveData;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AddRouteViewModel extends AndroidViewModel {
    private final MutableLiveData<AddRouteAction> mAction = new MutableLiveData<>();
    private final LocationLiveData locationLiveData;
    private final EventsRepository eventsRepository;
    public MutableLiveData<String> sourcePlace = new MutableLiveData<>();
    public MutableLiveData<String> destinationPlace = new MutableLiveData<>();
    public MutableLiveData<String> nameOfRoute = new MutableLiveData<>();
    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public AddRouteActivityListener addRouteActivityListener = null;

    public AddRouteViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        eventsRepository = new EventsRepository();
    }

    public void onReturnClicked() {
        mAction.setValue(new AddRouteAction(AddRouteAction.SHOW_ROUTES_LIST));
    }

    private LocationRequest drogopolexRuleToLocationRequest(DrogopolexRule rule) {
        if (rule instanceof DrogopolexPointRule) {
            LatLng latLng = ((DrogopolexPointRule) rule).getLatLng();
            return new LocationRequest(latLng.latitude, latLng.longitude);
        } else if (rule instanceof DrogopolexNameRule)
            return new LocationRequest(((DrogopolexNameRule) rule).getPlaceName());
        return null;
    }

    private GenerateRouteRequest.PointToAvoid drogopolexRuleToPointToAvoid(DrogopolexRule rule) {
        if (rule instanceof DrogopolexPointRule) {
            LatLng latLng = ((DrogopolexPointRule) rule).getLatLng();
            return new GenerateRouteRequest.PointToAvoid(latLng.latitude, latLng.longitude, 20);
        }
        return null;
    }

    public void onAddRouteClicked() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");

        LatLng sourceLatLng = addRouteActivityListener.getLatLngOfChosenPoint(true);
        LatLng destinationLatLng = addRouteActivityListener.getLatLngOfChosenPoint(false);

        String name = nameOfRoute.getValue();
        String source = sourcePlace.getValue();
        String dest = destinationPlace.getValue();

        List<DrogopolexRule> rules = addRouteActivityListener.getRules();

        List<LocationRequest> viaPoints = rules.stream()
                .filter(rule -> !rule.isAvoid())
                .map(this::drogopolexRuleToLocationRequest)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<GenerateRouteRequest.PointToAvoid> avoidPoints = rules.stream()
                .filter(DrogopolexRule::isAvoid)
                .map(this::drogopolexRuleToPointToAvoid)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<String> avoidEvents = rules.stream()
                .filter(DrogopolexEventTypeRule.class::isInstance)
                .map(rule -> ((DrogopolexEventTypeRule) rule).getEventType())
                .collect(Collectors.toList());
        GenerateRouteRequest.Avoid avoid = new GenerateRouteRequest.Avoid(avoidPoints, avoidEvents);

        LiveData<RouteValue> routeResponseLiveData;
        if (!isLatLngActive(sourceLatLng, source) && !isLatLngActive(destinationLatLng, dest)) {
            routeResponseLiveData =
                    eventsRepository.generateRoute(
                            new GenerateRouteRequest(name, source, dest, avoid, viaPoints),
                            userId, token);

        } else if (isLatLngActive(sourceLatLng, source) && !isLatLngActive(destinationLatLng, dest)) {
            routeResponseLiveData =
                    eventsRepository.generateRoute(
                            new GenerateRouteRequest(name, sourceLatLng, dest, avoid, viaPoints),
                            userId, token);

        } else if (!isLatLngActive(sourceLatLng, source) && isLatLngActive(destinationLatLng, dest)) {
            routeResponseLiveData =
                    eventsRepository.generateRoute(
                            new GenerateRouteRequest(name, source, destinationLatLng, avoid, viaPoints),
                            userId, token);

        } else {
            routeResponseLiveData =
                    eventsRepository.generateRoute(
                            new GenerateRouteRequest(name, sourceLatLng, destinationLatLng, avoid, viaPoints),
                            userId, token);
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
        mAction.setValue(new AddRouteAction(AddRouteAction.SHOW_ADD_RULE_POPUP));
    }

    public LiveData<AddRouteAction> getAction() {
        return mAction;
    }

    public LocationLiveData getLocationLiveData() {
        return locationLiveData;
    }
}
