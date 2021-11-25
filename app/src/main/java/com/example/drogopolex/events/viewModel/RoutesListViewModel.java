package com.example.drogopolex.events.viewModel;

import android.content.SharedPreferences;

import com.example.drogopolex.data.network.response.RoutesResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.RoutesListListener;
import com.example.drogopolex.events.utils.RoutesListAction;
import com.example.drogopolex.listeners.SharedPreferencesHolder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoutesListViewModel extends ViewModel {
    private MutableLiveData<RoutesListAction> mAction = new MutableLiveData<>();
    private LiveData<RoutesResponse> routesLiveData = new MutableLiveData<>();

    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public RoutesListListener routesListListener = null;

    private EventsRepository eventsRepository;

    public RoutesListViewModel() {
        eventsRepository = new EventsRepository();
    }

    public void onReturnClicked() {
        mAction.setValue(new RoutesListAction(RoutesListAction.SHOW_MAP));
    }

    public void onGoToAddRouteClicked() {
        mAction.setValue(new RoutesListAction(RoutesListAction.SHOW_ADD_ROUTE));
    }

    public LiveData<RoutesListAction> getAction() {
        return mAction;
    }

    public void fetchRoutes() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");

        routesLiveData = eventsRepository.getRoutes(userId, token);
        routesListListener.onGetRoutesSuccess(routesLiveData);
    }
}
