package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse implements TemplateResponse {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("token")
    @Expose
    private String token;


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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}