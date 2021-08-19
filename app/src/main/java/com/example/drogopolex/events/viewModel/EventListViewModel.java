package com.example.drogopolex.events.viewModel;

import com.example.drogopolex.adapters.EventListAdapter;
import com.example.drogopolex.data.repositories.EventsRepository;
import com.example.drogopolex.events.listeners.EventsListener;

import androidx.lifecycle.ViewModel;

public class EventListViewModel extends ViewModel {
    protected EventListAdapter adapter;
    protected EventsRepository eventsRepository;

    public EventsListener eventsListener = null;

    public String getTypeAt(int position) {
        if(adapter.getLocalDataSet()!= null
        && adapter.getLocalDataSet().size() > position) {
            return adapter.getLocalDataSet().get(position).getType();
        }
        return null;
    }

    public String getLocationAt(int position) {
        if(adapter.getLocalDataSet()!= null
                && adapter.getLocalDataSet().size() > position) {
            return adapter.getLocalDataSet().get(position).getLocation();
        }
        return null;
    }

    public String getVotesCountAt(int position) {
        if(adapter.getLocalDataSet()!= null
                && adapter.getLocalDataSet().size() > position) {
            return String.valueOf(adapter.getLocalDataSet().get(position).getVotesCount());
        }
        return null;
    }

    public void onUpVoteClick(int position) {

    }

    public void onDownVoteClick(int position) {

    }
}
