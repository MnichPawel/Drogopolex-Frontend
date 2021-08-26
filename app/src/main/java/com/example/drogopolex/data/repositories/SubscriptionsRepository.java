package com.example.drogopolex.data.repositories;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.SubscribeRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.ResponseType;
import com.example.drogopolex.data.network.response.SubscriptionsResponse;
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

        myApi.subscriptionSubscribe(token, user_id, new SubscribeRequest(localization, coordinates))
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
    public LiveData<SubscriptionsResponse> getSubscriptions(String token, String user_id) {
        final MutableLiveData<SubscriptionsResponse> eventsResponse = new MutableLiveData<>();

        myApi.subscriptionSubscriptions(token, user_id)
                .enqueue(new Callback<SubscriptionsResponse>() {
                    @Override
                    public void onResponse(Call<SubscriptionsResponse> call, Response<SubscriptionsResponse> response) {
                        if(response.isSuccessful()){
                            eventsResponse.setValue(response.body());
                        } else {
                            eventsResponse.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<SubscriptionsResponse> call, Throwable t) {
                        eventsResponse.setValue(null);
                    }
                });
        return eventsResponse;
    }
}
