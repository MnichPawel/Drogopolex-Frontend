package com.put.drogopolex.data.network;

import com.put.drogopolex.data.network.request.LoginRequest;
import com.put.drogopolex.data.network.request.LogoutRequest;
import com.put.drogopolex.data.network.request.RegisterRequest;
import com.put.drogopolex.data.network.response.BasicResponse;
import com.put.drogopolex.data.network.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MyApi {

    @POST("login")
    Call<LoginResponse> userLogin(@Body LoginRequest body);

    @POST("register")
    Call<BasicResponse> userRegister(@Body RegisterRequest body);

    @POST("logout")
    Call<BasicResponse> userLogout(@Body LogoutRequest body);

}
