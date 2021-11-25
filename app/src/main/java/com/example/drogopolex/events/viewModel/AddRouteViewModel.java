package com.example.drogopolex.events.viewModel;

import android.content.SharedPreferences;

import com.example.drogopolex.data.network.request.GenerateRouteRequest;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.AddRouteActivityListener;
import com.example.drogopolex.events.utils.AddRouteAction;
import com.example.drogopolex.listeners.SharedPreferencesHolder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddRouteViewModel extends ViewModel {
    private MutableLiveData<AddRouteAction> mAction = new MutableLiveData<>();

    public MutableLiveData<String> sourcePlace = new MutableLiveData<>();
    public MutableLiveData<String> destinationPlace = new MutableLiveData<>();
    public MutableLiveData<String> nameOfRoute = new MutableLiveData<>();

    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public AddRouteActivityListener addRouteActivityListener = null;

    private final EventsRepository eventsRepository;

    public AddRouteViewModel() {
        eventsRepository = new EventsRepository();
    }

    public void onReturnClicked() {
        mAction.setValue(new AddRouteAction(AddRouteAction.SHOW_ROUTES_LIST));
    }

    public void onAddRouteClicked() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String user_id = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");

        String name = nameOfRoute.getValue();
        String source = sourcePlace.getValue();
        String dest = destinationPlace.getValue();
        LiveData<RouteValue> routeResponseLiveData = eventsRepository.generateRoute(new GenerateRouteRequest(name, source, dest), user_id, token);

        addRouteActivityListener.onSuccessAddRoute(routeResponseLiveData);
    }

    public LiveData<AddRouteAction> getAction() {
        return mAction;
    }
}
