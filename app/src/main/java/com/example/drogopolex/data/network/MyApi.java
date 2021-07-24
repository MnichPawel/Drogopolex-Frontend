package com.example.drogopolex.data.network;

import com.example.drogopolex.data.network.request.BasicRequest;
import com.example.drogopolex.data.network.request.ChangeUserDataRequest;
import com.example.drogopolex.data.network.request.LoginRequest;
import com.example.drogopolex.data.network.request.RegisterRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.network.response.ProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MyApi {

    @POST("login")
    Call<LoginResponse> userLogin(@Body LoginRequest body);

    @POST("register")
    Call<BasicResponse> userRegister(@Body RegisterRequest body);

    @POST("logout")
    Call<BasicResponse> userLogout(@Body BasicRequest body);

    @POST("changeUserData")
    Call<BasicResponse> userChangeUserData(@Body ChangeUserDataRequest body);

    @POST("myProfile")
    Call<ProfileResponse> userGetUserData(@Body BasicRequest body);
}
