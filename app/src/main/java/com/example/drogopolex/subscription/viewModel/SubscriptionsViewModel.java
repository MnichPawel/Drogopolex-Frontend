package com.example.drogopolex.subscription.viewModel;

import android.app.Application;

import com.example.drogopolex.data.network.response.SubscriptionsResponse;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.subscription.utils.SubscriptionsAction;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SubscriptionsViewModel extends AndroidViewModel { //AndroidViewModel
    private MutableLiveData<SubscriptionsAction> mAction = new MutableLiveData<>();
    private LiveData<SubscriptionsResponse> subscriptionsLiveData = new MutableLiveData<>();
    public SharedPreferencesHolder sharedPreferencesHolder = null;

    private SubscriptionsRepository subscriptionsRepository;

    public SubscriptionsViewModel(Application application, String userId, String token) {
        super(application);
        subscriptionsRepository = new SubscriptionsRepository();
        requestSubscriptions(userId, token);
    }

    public void requestSubscriptions(String userId, String token) {
        subscriptionsLiveData = subscriptionsRepository.getSubscriptions(token, userId);
    }

    public void onReturnClicked() {
        mAction.setValue(new SubscriptionsAction(SubscriptionsAction.SHOW_LOGGED_IN));
    }


    public LiveData<SubscriptionsAction> getAction() {
        return mAction;
    }

    public LiveData<SubscriptionsResponse> getSubscriptionsLiveData() {
        return subscriptionsLiveData;
    }
}
