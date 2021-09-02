package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasicResponse implements TemplateResponse {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("error")
    @Expose
    private String error;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
