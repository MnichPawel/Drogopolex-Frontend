package com.example.drogopolex.data.repositories;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.ChangeUserDataRequest;
import com.example.drogopolex.data.network.request.LoginRequest;
import com.example.drogopolex.data.network.request.RegisterRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.network.response.ProfileResponse;
import com.example.drogopolex.data.network.response.ResponseType;
import com.example.drogopolex.data.network.utils.ErrorUtils;
import com.example.drogopolex.data.network.utils.RetrofitUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final MyApi myApi;

    public UserRepository() {
        myApi = RetrofitUtils.getRetrofitInstance().create(MyApi.class);
    }

    public LiveData<LoginResponse> userLogin(String email, String password) {
        final MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();

        myApi.userLogin(new LoginRequest(email, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            loginResponse.setValue(response.body());
                        } else {
                            loginResponse.setValue((LoginResponse) ErrorUtils.parseErrorResponse(response, ResponseType.LOGIN_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        loginResponse.setValue(null);
                    }
                });
        return loginResponse;
    }

    public LiveData<BasicResponse> userRegister(String email, String username, String password) {
        final MutableLiveData<BasicResponse> registerResponse = new MutableLiveData<>();

        myApi.userRegister(new RegisterRequest(email, username, password))
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if (response.isSuccessful()) {
                            registerResponse.setValue(response.body());
                        } else {
                            registerResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        registerResponse.setValue(null);
                    }
                });
        return registerResponse;
    }

    public LiveData<BasicResponse> userLogout(String userId, String token) {
        final MutableLiveData<BasicResponse> logoutResponse = new MutableLiveData<>();

        myApi.userLogout(token, userId)
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if (response.isSuccessful()) {
                            logoutResponse.setValue(response.body());
                        } else {
                            logoutResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        logoutResponse.setValue(null);
                    }
                });
        return logoutResponse;
    }

    public LiveData<BasicResponse> userChangeUserData(String userId, String token, String changedValue, String newValue) {
        final MutableLiveData<BasicResponse> changeUserDataResponse = new MutableLiveData<>();

        myApi.userChangeUserData(token, userId, new ChangeUserDataRequest(changedValue, newValue))
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if (response.isSuccessful()) {
                            changeUserDataResponse.setValue(response.body());
                        } else {
                            changeUserDataResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        changeUserDataResponse.setValue(null);
                    }
                });
        return changeUserDataResponse;
    }

    public LiveData<ProfileResponse> userGetUserData(String userId, String token) {
        final MutableLiveData<ProfileResponse> getUserDataResponse = new MutableLiveData<>();

        myApi.userGetUserData(token, userId)
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                        if (response.isSuccessful()) {
                            getUserDataResponse.setValue(response.body());
                        } else {
                            getUserDataResponse.setValue((ProfileResponse) ErrorUtils.parseErrorResponse(response, ResponseType.PROFILE_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call, Throwable t) {
                        getUserDataResponse.setValue(null);
                    }
                });
        return getUserDataResponse;
    }
}
