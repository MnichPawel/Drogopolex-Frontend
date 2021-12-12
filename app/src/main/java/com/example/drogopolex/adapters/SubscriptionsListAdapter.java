package com.example.drogopolex.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsListAdapter extends RecyclerView.Adapter<SubscriptionsListAdapter.ViewHolder> {
    private static List<DrogopolexSubscription> localDataSubs;
    private final Context context;
    public SubscriptionsListener subscriptionsListener;
    private final SubscriptionsRepository subscriptionsRepository;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subscriptionText;
        private final Button unsubscribe;

        public ViewHolder(View view) {
            super(view);

            subscriptionText = (TextView) view.findViewById(R.id.subscription_row_text);
            unsubscribe = (Button) view.findViewById(R.id.unsubBtn);
            unsubscribe.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                DrogopolexSubscription drogopolexSubscription = localDataSubs.get(adapterPosition);
                unsubscribeRequest(drogopolexSubscription.getId_sub().toString(), adapterPosition);
            });
        }

        public TextView getSubscriptionTextView() {
            return subscriptionText;
        }

        public Button getUnsubscribeButton() {
            return unsubscribe;
        }
    }

    public SubscriptionsListAdapter(List<DrogopolexSubscription> data, Context context) {
        localDataSubs = data;
        this.context = context;
        this.subscriptionsRepository = new SubscriptionsRepository();
    }

    @NonNull
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
        if(localDataSubs.get(position).getRec()) {
            viewHolder.getUnsubscribeButton().setText("DODAJ");

            Drawable buttonDrawable = viewHolder.getUnsubscribeButton().getBackground();
            buttonDrawable = DrawableCompat. wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.parseColor("#006400"));
            viewHolder.getUnsubscribeButton().setBackground(buttonDrawable);

            viewHolder.getUnsubscribeButton().setOnClickListener(v -> {
                DrogopolexSubscription dSubscription = localDataSubs.get(position);
                subscribeRequest(dSubscription.getLocation(), position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSubs.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void unsubscribeRequest(String subId, int subIndex){
        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        LiveData<BasicResponse> responseLiveData = subscriptionsRepository.unsubscribeSubscriptions(token, userId, subId);
        subscriptionsListener.onSubscriptionsSuccess(responseLiveData, subIndex);
        this.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void subscribeRequest(String localization, int subIndex){
        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        LiveData<BasicResponse> responseLiveData = subscriptionsRepository.subscriptionSubscribe(localization, "", userId, token);
        subscriptionsListener.onSubscriptionsSuccess(responseLiveData, subIndex);
        this.notifyDataSetChanged();
    }
}