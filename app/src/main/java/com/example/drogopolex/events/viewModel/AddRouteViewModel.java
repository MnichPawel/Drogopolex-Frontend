package com.example.drogopolex.events.viewModel;

import com.example.drogopolex.events.utils.RoutesListAction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddRouteViewModel extends ViewModel {
    private MutableLiveData<RoutesListAction> mAction = new MutableLiveData<>();

    public MutableLiveData<String> sourcePlace = new MutableLiveData<>();
    public MutableLiveData<String> destinationPlace = new MutableLiveData<>();

    public void onReturnClicked() {
        mAction.setValue(new RoutesListAction(RoutesListAction.SHOW_MAP));
    }

    public void onAddRouteClicked() {

    }

    public LiveData<RoutesListAction> getAction() {
        return mAction;
    }
}
