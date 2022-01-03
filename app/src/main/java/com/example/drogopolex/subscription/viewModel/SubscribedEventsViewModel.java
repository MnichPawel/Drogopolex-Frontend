package com.example.drogopolex.subscription.viewModel;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.subscription.utils.SubscribedEventsAction;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SubscribedEventsViewModel extends AndroidViewModel {
    private MutableLiveData<SubscribedEventsAction> mAction = new MutableLiveData<>();
    private LiveData<EventsResponse> eventsLiveData = new MutableLiveData<>();
    private SubscriptionsRepository subscriptionsRepository;

    public OnSuccessListener<LiveData<EventsResponse>> onSuccessListener = null;
    public SharedPreferencesHolder sharedPreferencesHolder = null;


    public SubscribedEventsViewModel(Application application) {
        super(application);
        subscriptionsRepository = new SubscriptionsRepository();
    }

    public void requestSubscribedEvents() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");
        eventsLiveData = subscriptionsRepository.getSubscribedEvents(token, userId);
        onSuccessListener.onSuccess(eventsLiveData);
    }

    public void onAddClicked() {
        mAction.setValue(new SubscribedEventsAction(SubscribedEventsAction.ADD_SUBSCRIPTION));
    }

    public void onReturnClicked() {
        mAction.setValue(new SubscribedEventsAction(SubscribedEventsAction.SHOW_SUBSCRIPTIONS));
    }

    public LiveData<SubscribedEventsAction> getAction() {
        return mAction;
    }
}