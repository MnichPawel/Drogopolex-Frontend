package com.example.drogopolex.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.PointsOfInterestRequest;
import com.example.drogopolex.data.network.response.PointsOfInterestResponse;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.data.network.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationRepository {
    private MyApi myApi;

    public RecommendationRepository() {
        myApi = RetrofitUtils.getRetrofitInstance().create(MyApi.class);
    }

    public LiveData<PointsOfInterestResponse> getPointsFromUserArea(String userId, String token, String latitude, String longitude) {
        final MutableLiveData<PointsOfInterestResponse> points = new MutableLiveData<>();

        myApi.recommendationPointsOfInterest(token, userId, new PointsOfInterestRequest(latitude, longitude))
                .enqueue(new Callback<PointsOfInterestResponse>() {
                    @Override
                    public void onResponse(Call<PointsOfInterestResponse> call, Response<PointsOfInterestResponse> response) {
                        if(response.isSuccessful()){
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

    public LiveData<RouteValue> getRecommendedRoute(String userid, String token) {
        final MutableLiveData<RouteValue> route = new MutableLiveData<>();

        myApi.recommendationGetRecommendedRoute(token, userid)
                .enqueue(new Callback<RouteValue>() {
                    @Override
                    public void onResponse(Call<RouteValue> call, Response<RouteValue> response) {
                        if(response.isSuccessful()) {
                            Log.d("getRecommendedRoute_RECOM", "TRUE");
                            route.setValue(response.body());
                        } else {
                            Log.d("getRecommendedRoute_RECOM", "FALSE");
                            route.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RouteValue> call, Throwable t) {
                        route.setValue(null);
                    }
                });
        return route;
    }
}
