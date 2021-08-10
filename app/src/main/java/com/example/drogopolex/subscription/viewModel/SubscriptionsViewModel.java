package com.example.drogopolex.subscription.viewModel;

import android.app.Application;

import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.SubscriptionsResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.services.LocationLiveData;
import com.example.drogopolex.subscription.utils.SubscriptionsAction;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SubscriptionsViewModel extends AndroidViewModel {
    private MutableLiveData<SubscriptionsAction> mAction = new MutableLiveData<>();
    private LiveData<SubscriptionsResponse> subscriptionsLiveData = new MutableLiveData<>();
    private LocationLiveData locationLiveData;

    public OnSuccessListener<LiveData<SubscriptionsResponse>> onSuccessListener = null;

    private SubscriptionsRepository subscriptionsRepository;

    public SubscriptionsViewModel(Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        subscriptionsRepository = new SubscriptionsRepository();
    }

    public void onLocationChanged(LocationDetails location) {
        subscriptionsLiveData = subscriptionsRepository.getSubscriptions(location.getLatitude(), location.getLongitude());
        onSuccessListener.onSuccess(subscriptionsLiveData);
    }

    public void onReturnClicked() {
        mAction.setValue(new SubscriptionsAction(SubscriptionsAction.SHOW_LOGGED_IN));
    }


    public LocationLiveData getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<SubscriptionsAction> getAction() {
        return mAction;
    }
}
