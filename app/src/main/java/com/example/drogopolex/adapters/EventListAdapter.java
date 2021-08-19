package com.example.drogopolex.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.drogopolex.BR;
import com.example.drogopolex.events.viewModel.EventListViewModel;
import com.example.drogopolex.model.DrogopolexEvent;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private ArrayList<DrogopolexEvent> localDataSet;
    private EventListViewModel viewModel;

    public EventListAdapter(EventListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        ViewDataBinding dataBinding = DataBindingUtil.inflate(layoutInflater, viewType, viewGroup, false);

        return new ViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.bind(viewModel, position);
    }

    @Override
    public int getItemCount() {
        return localDataSet == null ? 0 : localDataSet.size();
    }

    public void setLocalDataSet(ArrayList<DrogopolexEvent> localDataSet) {
        this.localDataSet = localDataSet;
    }

    public ArrayList<DrogopolexEvent> getLocalDataSet() {
        return localDataSet;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(EventListViewModel viewModel, int position) {
            binding.setVariable(BR.viewModel, viewModel);
            binding.setVariable(BR.position, position);
            binding.executePendingBindings();
        }
    }
}