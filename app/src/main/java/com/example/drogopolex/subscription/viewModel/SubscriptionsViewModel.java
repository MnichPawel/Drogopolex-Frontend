package com.example.drogopolex.subscription.viewModel;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.drogopolex.constants.AppConstant;
import com.example.drogopolex.data.network.response.SubscriptionsResponse;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.subscription.listeners.SubscriptionListListener;
import com.example.drogopolex.subscription.utils.SubscriptionsAction;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SubscriptionsViewModel extends AndroidViewModel {
    private final MutableLiveData<SubscriptionsAction> mAction = new MutableLiveData<>();
    private final SubscriptionsRepository subscriptionsRepository;
    public SharedPreferencesHolder sharedPreferencesHolder = null;
    public SubscriptionListListener subscriptionListListener = null;
    private LiveData<SubscriptionsResponse> subscriptionsLiveData = new MutableLiveData<>();


    public SubscriptionsViewModel(Application application) {
        super(application);
        subscriptionsRepository = new SubscriptionsRepository();
    }

    public void requestSubscriptions() {
        SharedPreferences sharedPreferences = sharedPreferencesHolder.getSharedPreferences();
        String userId = sharedPreferences.getString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        String token = sharedPreferences.getString(AppConstant.TOKEN_SHARED_PREFERENCES, "");
        subscriptionsLiveData = subscriptionsRepository.getSubscriptions(token, userId);
        subscriptionListListener.onSuccess(subscriptionsLiveData);
    }

    public void onReturnClicked() {
        mAction.setValue(new SubscriptionsAction(SubscriptionsAction.SHOW_MAP));
    }

    public void onGoToSubscribeClicked() {
        mAction.setValue(new SubscriptionsAction(SubscriptionsAction.SHOW_SUBSCRIBE));
    }

    public void onGoToSubscribedEvents() {
        mAction.setValue(new SubscriptionsAction(SubscriptionsAction.SHOW_SUBSCRIBED_EVENTS));
    }

    public LiveData<SubscriptionsAction> getAction() {
        return mAction;
    }

}
