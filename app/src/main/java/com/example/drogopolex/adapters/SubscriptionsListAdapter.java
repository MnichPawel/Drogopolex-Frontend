package com.example.drogopolex.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.drogopolex.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsListAdapter extends RecyclerView.Adapter<SubscriptionsListAdapter.ViewHolder> {
    private ArrayList<String> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subscriptionText;

        public ViewHolder(View view) {
            super(view);

            subscriptionText = (TextView) view.findViewById(R.id.subscription_row_text);
        }

        public TextView getSubscriptionTextView() {
            return subscriptionText;
        }
    }

    public SubscriptionsListAdapter(ArrayList<String> dataSet) {
        localDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.subscriptions_adapter_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getSubscriptionTextView().setText(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}