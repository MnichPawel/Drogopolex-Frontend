package com.example.drogopolex.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.drogopolex.R;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.model.DrogopolexSubscription;
import com.example.drogopolex.subscription.listeners.SubscriptionsListener;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsListAdapter extends RecyclerView.Adapter<SubscriptionsListAdapter.ViewHolder> {
    private static ArrayList<DrogopolexSubscription> localDataSubs;
    private final Context context;
    public Integer indexToUnsubscribeTo;
    public SubscriptionsListener subscriptionsListener;
    private SubscriptionsRepository subscriptionsRepository;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subscriptionText;


        public ViewHolder(View view) {
            super(view);

            subscriptionText = (TextView) view.findViewById(R.id.subscription_row_text);
            Button unsubscribe = (Button) view.findViewById(R.id.unsubBtn);
            unsubscribe.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    DrogopolexSubscription drogopolexSubscription = localDataSubs.get(adapterPosition);
                    unsubscribeRequest(drogopolexSubscription.getId_sub().toString(), adapterPosition);
                }
            });
        }

        public TextView getSubscriptionTextView() {
            return subscriptionText;
        }
    }

    public SubscriptionsListAdapter( ArrayList<DrogopolexSubscription> data, Context context) {
        localDataSubs = data;
        this.context = context;
        this.subscriptionsRepository = new SubscriptionsRepository();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_subscriptions_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        DrogopolexSubscription drogopolexSubscription = localDataSubs.get(position);
        viewHolder.getSubscriptionTextView().setText(drogopolexSubscription.getLocation());
    }

    @Override
    public int getItemCount() {
        return localDataSubs.size();
    }

    public void unsubscribeRequest(String subId, int subIndex){
        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        LiveData<BasicResponse> responseLiveData = subscriptionsRepository.unsubscribeSubscriptions(token, userId, subId);
        subscriptionsListener.onSubscriptionsSuccess(responseLiveData, subIndex);
    }
}