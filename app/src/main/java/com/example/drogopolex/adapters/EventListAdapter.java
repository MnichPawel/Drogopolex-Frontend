package com.example.drogopolex.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.example.drogopolex.model.DrogopolexEvent;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private ArrayList<DrogopolexEvent> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView typeText;
        private final TextView locationText;

        public ViewHolder(View view) {
            super(view);

            typeText = (TextView) view.findViewById(R.id.typeText);
            locationText = (TextView) view.findViewById(R.id.locationText);
        }

        public TextView getTypeTextView() {
            return typeText;
        }

        public TextView getLocationTextView() {
            return locationText;
        }
    }

    public EventListAdapter(ArrayList<DrogopolexEvent> dataSet) {
        localDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTypeTextView().setText(localDataSet.get(position).GetType());
        viewHolder.getLocationTextView().setText(localDataSet.get(position).GetLocation());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}