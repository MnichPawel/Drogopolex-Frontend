package com.example.drogopolex.subscription.viewModel;

import android.app.Application;

import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.subscription.utils.SubscribedEventsAction;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SubscribedEventsViewModel  extends AndroidViewModel {
    private MutableLiveData<SubscribedEventsAction> mAction = new MutableLiveData<>();
    private LiveData<EventsResponse> eventsLiveData = new MutableLiveData<>();
    private SubscriptionsRepository subscriptionsRepository;


    public SubscribedEventsViewModel(Application application, String userId, String token) {
        super(application);
        subscriptionsRepository = new SubscriptionsRepository();
        requestSubscribedEvents(userId, token);
    }
    public void requestSubscribedEvents(String userId, String token) {
        eventsLiveData = subscriptionsRepository.getSubscribedEvents(token, userId);
    }
    public void onAddClicked() {
        mAction.setValue(new SubscribedEventsAction(SubscribedEventsAction.ADD_SUBSCRIPTION));
    }

    public void onListClicked() {
        mAction.setValue(new SubscribedEventsAction(SubscribedEventsAction.SHOW_SUBSCRIPTIONS));
    }

    public void onReturnClicked() {
        mAction.setValue(new SubscribedEventsAction(SubscribedEventsAction.GO_TO_MAIN_MENU));
    }

    public LiveData<SubscribedEventsAction> getAction() {
        return mAction;
    }
}
