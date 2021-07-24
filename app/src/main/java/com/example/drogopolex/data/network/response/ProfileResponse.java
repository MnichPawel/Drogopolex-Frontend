package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse implements TemplateResponse {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("errorString")
    @Expose
    private String errorString;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nazwa")
    @Expose
    private String nazwa;
    @SerializedName("email")
    @Expose
    private String email;


    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
