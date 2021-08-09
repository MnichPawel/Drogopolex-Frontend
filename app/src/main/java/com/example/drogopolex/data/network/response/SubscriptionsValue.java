package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionsValue {
    @SerializedName("localization")
    @Expose
    private String localization;
    @SerializedName("id_sub")
    @Expose
    private String id_sub;

    public String getId() {
        return id_sub;
    }

    public void setId(String id_sub) {
        this.id_sub = id_sub;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }
}
