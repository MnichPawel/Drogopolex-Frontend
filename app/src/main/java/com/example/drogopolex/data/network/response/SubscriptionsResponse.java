package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscriptionsResponse {
    @SerializedName("subscriptions")
    private List<SubscriptionsValue> subscriptions;

    public List<SubscriptionsValue> getSubscriptionss() {
        return subscriptions;
    }
}
