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
import com.example.drogopolex.data.repositories.SubscriptionsRepository;
import com.example.drogopolex.model.DrogopolexSubscription;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsListAdapter extends RecyclerView.Adapter<SubscriptionsListAdapter.ViewHolder> {
    private static List<DrogopolexSubscription> localDataSubs;
    private final Context context;
    private final SubscriptionsRepository subscriptionsRepository;

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
        if (Boolean.TRUE.equals(localDataSubs.get(position).getRec())) {
            viewHolder.getUnsubscribeButton().setText("Dodaj");

            Drawable buttonDrawable = viewHolder.getUnsubscribeButton().getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.parseColor("#006400"));
            viewHolder.getUnsubscribeButton().setBackground(buttonDrawable);

            viewHolder.getUnsubscribeButton().setOnClickListener(v -> {
                DrogopolexSubscription dSubscription = localDataSubs.get(position);
                subscribeRequest(dSubscription.getLocation());

                localDataSubs.get(position).setRec(false);
                notifyItemChanged(position);
            });
        } else {
            viewHolder.getUnsubscribeButton().setText("Usuń");

            Drawable buttonDrawable = viewHolder.getUnsubscribeButton().getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.parseColor("#B00020"));
            viewHolder.getUnsubscribeButton().setBackground(buttonDrawable);

            viewHolder.getUnsubscribeButton().setOnClickListener(v -> {
                DrogopolexSubscription dSubscription = localDataSubs.get(position);
                unsubscribeRequest(dSubscription.getId_sub().toString());

                localDataSubs.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, localDataSubs.size());
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSubs.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void unsubscribeRequest(String subId) {
        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        subscriptionsRepository.unsubscribeSubscriptions(token, userId, subId);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void subscribeRequest(String localization) {
        SharedPreferences sp = context.getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String userId = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        subscriptionsRepository.subscriptionSubscribe(localization, userId, token);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subscriptionText;
        private final Button unsubscribe;

        public ViewHolder(View view) {
            super(view);

            subscriptionText = view.findViewById(R.id.subscription_row_text);
            unsubscribe = view.findViewById(R.id.unsubBtn);
        }

        public TextView getSubscriptionTextView() {
            return subscriptionText;
        }

        public Button getUnsubscribeButton() {
            return unsubscribe;
        }
    }
}