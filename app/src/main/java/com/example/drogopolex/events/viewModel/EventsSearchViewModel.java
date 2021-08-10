package com.example.drogopolex.events.viewModel;

import com.example.drogopolex.constants.EventTypes;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.EventsListener;
import com.example.drogopolex.events.listeners.SpinnerHolder;
import com.example.drogopolex.events.utils.EventsSearchAction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventsSearchViewModel extends ViewModel {
    private MutableLiveData<EventsSearchAction> mAction = new MutableLiveData<>();
    private LiveData<EventsResponse> eventsLiveData = new MutableLiveData<>();
    public MutableLiveData<String> localization = new MutableLiveData<>();

    private EventsRepository eventsRepository;

    public EventsListener eventsListener = null;
    public SpinnerHolder spinnerHolder = null;

    public EventsSearchViewModel() {
        this.eventsRepository = new EventsRepository();
    }
    public void onReturnClicked() {
        mAction.setValue(new EventsSearchAction(EventsSearchAction.SHOW_LOGGED_IN));
    }

    public void onSearchClicked() {
        String localizationValue = localization.getValue();
        String selectedEventType = spinnerHolder.getSelectedItem();

        if(localizationValue == null || selectedEventType == null ||
                (localizationValue.isEmpty() && (selectedEventType.isEmpty() ||
                selectedEventType.equals(EventTypes.getEventTypes().get(0))))) {
            eventsListener.onFailure("Nieprawidłowa lokalizacja lub typ zdarzenia.");
        } else if(selectedEventType.isEmpty() || selectedEventType.equals(EventTypes.getEventTypes().get(0))) {
            LiveData<EventsResponse> getEventsByLocalization = eventsRepository.getEventsByLocalization(localizationValue);
            eventsListener.onSuccess(getEventsByLocalization);
        } else if(localizationValue.isEmpty()) {
            LiveData<EventsResponse> getEventsByType = eventsRepository.getEventsByType(selectedEventType);
            eventsListener.onSuccess(getEventsByType);
        } else {
            LiveData<EventsResponse> getEventsByTypeAndLocalization = eventsRepository.getEventsByTypeAndLocalization(selectedEventType, localizationValue);
            eventsListener.onSuccess(getEventsByTypeAndLocalization);
        }
    }

    public LiveData<EventsSearchAction> getAction() {
        return mAction;
    }
}
