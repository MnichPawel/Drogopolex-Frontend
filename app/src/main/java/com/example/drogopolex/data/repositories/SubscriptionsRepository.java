package com.example.drogopolex.data.repositories;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.SubscribeRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.ResponseType;
import com.example.drogopolex.data.network.utils.ErrorUtils;
import com.example.drogopolex.data.network.utils.RetrofitUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionsRepository {

    private MyApi myApi;

    public SubscriptionsRepository(){
        myApi =  RetrofitUtils.getRetrofitInstance().create(MyApi.class);
    }

    public LiveData<BasicResponse> subscriptionSubscribe(String localization, String coordinates, String user_id, String token) {
        final MutableLiveData<BasicResponse> userSubscribeResponse = new MutableLiveData<>();

        myApi.subscriptionSubscribe(new SubscribeRequest(localization, coordinates,user_id, token))
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if(response.isSuccessful()){
                            userSubscribeResponse.setValue(response.body());
                        } else {
                            userSubscribeResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        userSubscribeResponse.setValue(null);
                    }
                });
        return userSubscribeResponse;
    }
}
