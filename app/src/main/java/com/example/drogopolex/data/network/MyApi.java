package com.example.drogopolex.data.network;

import com.example.drogopolex.data.network.request.LoginRequest;
import com.example.drogopolex.data.network.request.RegisterRequest;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.network.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MyApi {

    @POST("login")
    Call<LoginResponse> userLogin(@Body LoginRequest body);

    @POST("register")
    Call<RegisterResponse> userRegister(@Body RegisterRequest body);

}
