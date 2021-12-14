package com.example.drogopolex.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.PointsOfInterestRequest;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.PointsOfInterestResponse;
import com.example.drogopolex.data.network.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointsOfInterestRepository {
    private MyApi myApi;

    public PointsOfInterestRepository() {
        myApi = RetrofitUtils.getRetrofitInstance().create(MyApi.class);
    }

    public LiveData<PointsOfInterestResponse> getPointsFromUserArea(String userId, String token, String latitude, String longitude) {
        final MutableLiveData<PointsOfInterestResponse> points = new MutableLiveData<>();

        Log.d("PointsOfInterestRepository", latitude + " " + longitude);
        myApi.recommendationPointsOfInterest(token, userId, new PointsOfInterestRequest(latitude, longitude))
                .enqueue(new Callback<PointsOfInterestResponse>() {
                    @Override
                    public void onResponse(Call<PointsOfInterestResponse> call, Response<PointsOfInterestResponse> response) {
                        if(response.isSuccessful()){
                            Log.d("PointsOfInterestRepository", "GITUWA");
                            points.setValue(response.body());
                        } else {
                            points.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<PointsOfInterestResponse> call, Throwable t) {
                        points.setValue(null);
                    }
                });
        return points;
    }
}
