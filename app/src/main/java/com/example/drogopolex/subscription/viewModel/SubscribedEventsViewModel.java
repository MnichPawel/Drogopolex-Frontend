package com.example.drogopolex.subscription.viewModel;

import android.app.Application;

import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.services.LocationLiveData;
import com.example.drogopolex.subscription.utils.SubscribedEventsAction;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SubscribedEventsViewModel  extends AndroidViewModel {
    private MutableLiveData<EventsAction> mAction = new MutableLiveData<>();
    private LiveData<EventsResponse> eventsLiveData = new MutableLiveData<>();
   // private LocationLiveData locationLiveData;

    public OnSuccessListener<LiveData<EventsResponse>> onSuccessListener = null;

    //private EventsRepository eventsRepository;
    private SubscriptionsRepository subscriptionsRepository;
    public SubscribedEventsViewModel(Application application, String userId, String token) {
        super(application);
        //locationLiveData = new LocationLiveData(application);
        //eventsRepository = new EventsRepository();
        subscriptionsRepository = new SubscriptionsRepository();
        requestSubscribedEvents(userId, token);
    }
    public void requestSubscribedEvents(String userId, String token) {
        eventsLiveData = subscriptionsRepository.getSubscribedEvents(token, userId);
    }
    public void onAddClicked() {
        mAction.setValue(new EventsAction(SubscribedEventsAction.ADD_SUBSCRIPTION));
    }

    public void onListClicked() {
        mAction.setValue(new EventsAction(SubscribedEventsAction.SHOW_SUBSCRIPTIONS));
    }

    public void onReturnClicked() {
        mAction.setValue(new EventsAction(SubscribedEventsAction.GO_TO_MAIN_MENU));
    }

    public LiveData<EventsAction> getAction() {
        return mAction;
    }
}
