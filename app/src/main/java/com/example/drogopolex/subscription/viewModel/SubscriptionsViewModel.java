package com.example.drogopolex.subscription.viewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.SubscriptionsResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.LocationDetails;
import com.example.drogopolex.services.LocationLiveData;
import com.example.drogopolex.subscription.utils.SubscriptionsAction;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SubscriptionsViewModel extends AndroidViewModel { //AndroidViewModel
    private MutableLiveData<SubscriptionsAction> mAction = new MutableLiveData<>();
    private LiveData<SubscriptionsResponse> subscriptionsLiveData = new MutableLiveData<>();
    private LocationLiveData locationLiveData;
    public SharedPreferencesHolder sharedPreferencesHolder = null;

    public OnSuccessListener<LiveData<SubscriptionsResponse>> onSuccessListener = null;

    private SubscriptionsRepository subscriptionsRepository;

    public SubscriptionsViewModel(Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application);
        subscriptionsRepository = new SubscriptionsRepository();
        //requestSubscriptions();
    }

    public void requestSubscriptions() { //LocationDetails location
        Log.d("myTag", "Wyslanie prosby o cale te");
        SharedPreferences sp = sharedPreferencesHolder.getSharedPreferences();
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");
        subscriptionsLiveData = subscriptionsRepository.getSubscriptions(token, user_id);
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
