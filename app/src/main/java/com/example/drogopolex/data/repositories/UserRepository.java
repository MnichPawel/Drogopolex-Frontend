package com.example.drogopolex.data.repositories;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.LoginRequest;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.network.utils.ErrorUtils;
import com.example.drogopolex.data.network.utils.RetrofitUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private MyApi myApi;

    public UserRepository(){
        myApi =  RetrofitUtils.getRetrofitInstance().create(MyApi.class);
    }

    public LiveData<LoginResponse> userLogin(String email, String password){
        final MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();


        myApi.userLogin(new LoginRequest(email, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(response.isSuccessful()){
                            loginResponse.setValue(response.body());
                        } else {
                            loginResponse.setValue(ErrorUtils.parseErrorResponse(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        loginResponse.setValue(null);
                    }
                });
        return loginResponse;
    }
}
