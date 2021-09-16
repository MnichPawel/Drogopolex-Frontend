package com.example.drogopolex.subscription.viewModel;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.drogopolex.data.network.response.SubscriptionsResponse;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.subscription.listeners.SubscriptionListListener;
import com.example.drogopolex.subscription.utils.SubscriptionsAction;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SubscriptionsViewModel extends AndroidViewModel { //AndroidViewModel
    private MutableLiveData<SubscriptionsAction> mAction = new MutableLiveData<>();
    private LiveData<SubscriptionsResponse> subscriptionsLiveData = new MutableLiveData<>();

    public SharedPreferencesHolder sharedPreferencesHolder = null;
//    public OnSuccessListener<LiveData<SubscriptionsResponse>> onSuccessListener = null;
    public SubscriptionListListener subscriptionListListener = null;


    private SubscriptionsRepository subscriptionsRepository;


    public SubscriptionsViewModel(Application application) {
        super(application);
        subscriptionsRepository = new SubscriptionsRepository();
    }

    public void requestSubscriptions() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString("user_id", "");
        String token = sharedPreferences.getString("token", "");
        subscriptionsLiveData = subscriptionsRepository.getSubscriptions(token, userId);
        subscriptionListListener.onSuccess(subscriptionsLiveData);
    }

    public void onReturnClicked() {
        mAction.setValue(new SubscriptionsAction(SubscriptionsAction.SHOW_LOGGED_IN));
    }


    public LiveData<SubscriptionsAction> getAction() {
        return mAction;
    }

}
