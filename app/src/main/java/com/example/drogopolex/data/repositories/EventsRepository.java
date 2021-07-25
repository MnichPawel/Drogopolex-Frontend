package com.example.drogopolex.data.repositories;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.EventsByGpsRequest;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.utils.RetrofitUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsRepository {
    private MyApi myApi;

    public EventsRepository(){
        myApi =  RetrofitUtils.getRetrofitInstance().create(MyApi.class);
    }

    public LiveData<EventsResponse> getEventsFromUserArea(String latitude, String longitude) {
        final MutableLiveData<EventsResponse> eventsResponse = new MutableLiveData<>();

        myApi.eventsGetFromUserArea(new EventsByGpsRequest(latitude, longitude))
                .enqueue(new Callback<EventsResponse>() {
                    @Override
                    public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                        if(response.isSuccessful()){
                            eventsResponse.setValue(response.body());
                        } else {
                            eventsResponse.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<EventsResponse> call, Throwable t) {
                        eventsResponse.setValue(null);
                    }
                });
        return eventsResponse;
    }
}
