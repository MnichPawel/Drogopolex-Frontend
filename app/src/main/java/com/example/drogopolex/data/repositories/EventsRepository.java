package com.example.drogopolex.data.repositories;

import android.util.Log;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.AddEventRequest;
import com.example.drogopolex.data.network.request.EventsByGpsRequest;
import com.example.drogopolex.data.network.request.GenerateRouteRequest;
import com.example.drogopolex.data.network.request.GetRouteRequest;
import com.example.drogopolex.data.network.request.RemoveRouteRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.ResponseType;
import com.example.drogopolex.data.network.response.RouteValue;
import com.example.drogopolex.data.network.response.RoutesResponse;
import com.example.drogopolex.data.network.utils.ErrorUtils;
import com.example.drogopolex.data.network.utils.RetrofitUtils;
import com.example.drogopolex.model.LocationDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsRepository {
    private final MyApi myApi;

    public EventsRepository(){
        myApi =  RetrofitUtils.getRetrofitInstance().create(MyApi.class);
    }

    public LiveData<EventsResponse> getEventsFromUserArea(String userId, String token, String latitude, String longitude) {
        final MutableLiveData<EventsResponse> eventsResponse = new MutableLiveData<>();

        Log.d("EventsRepository", latitude + "  " + longitude);
        myApi.eventsGetEvents(userId, token, new EventsByGpsRequest(latitude, longitude))
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

    public LiveData<BasicResponse> addEventByGps(LocationDetails locationDetails, String eventType, String userId, String token) {
        final MutableLiveData<BasicResponse> addEventResponse = new MutableLiveData<>();

        myApi.eventsAddEvent(token, userId,
                new AddEventRequest(
                locationDetails.getLatitude(),
                locationDetails.getLongitude(),
                eventType
        )).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if(response.isSuccessful()){
                    addEventResponse.setValue(response.body());
                } else {
                    addEventResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                addEventResponse.setValue(null);
            }
        });
        return addEventResponse;
    }
  
    public LiveData<RouteValue> generateRoute(GenerateRouteRequest request, String userId, String token) {
        final MutableLiveData<RouteValue> generateRouteResponse = new MutableLiveData<>();

        myApi.eventsGenerateRoute(token, userId, request)
                .enqueue(new Callback<RouteValue>() {
                    @Override
                    public void onResponse(Call<RouteValue> call, Response<RouteValue> response) {
                        if (response.isSuccessful()) {
                            generateRouteResponse.setValue(response.body());
                        } else {
                            generateRouteResponse.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RouteValue> call, Throwable t) {
                        generateRouteResponse.setValue(null);
                    }
                });
        return generateRouteResponse;
    }

    public LiveData<RouteValue> getRoute(String userId, String token, String routeId) {
        final MutableLiveData<RouteValue> getRouteResponse = new MutableLiveData<>();

        myApi.eventsGetRoute(token, userId, new GetRouteRequest(routeId))
                .enqueue(new Callback<RouteValue>() {
                    @Override
                    public void onResponse(Call<RouteValue> call, Response<RouteValue> response) {
                        if (response.isSuccessful()) {
                            getRouteResponse.setValue(response.body());
                        } else {
                            getRouteResponse.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RouteValue> call, Throwable t) {
                        getRouteResponse.setValue(null);
                    }
                });
        return getRouteResponse;
    }

    public LiveData<RoutesResponse> getRoutes(String userId, String token) {
        final MutableLiveData<RoutesResponse> getRoutesResponse = new MutableLiveData<>();

        myApi.eventsGetRoutes(token, userId)
                .enqueue(new Callback<RoutesResponse>() {
                    @Override
                    public void onResponse(Call<RoutesResponse> call, Response<RoutesResponse> response) {
                        if (response.isSuccessful()) {
                            getRoutesResponse.setValue(response.body());
                        } else {
                            getRoutesResponse.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RoutesResponse> call, Throwable t) {
                        getRoutesResponse.setValue(null);
                    }
                });
        return getRoutesResponse;
    }

    public LiveData<BasicResponse> removeRoute(String userId, String token, String routeId) {
        final MutableLiveData<BasicResponse> removeRouteResponse = new MutableLiveData<>();

        myApi.eventsRemoveRoute(token, userId, new RemoveRouteRequest(routeId))
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if (response.isSuccessful()) {
                            removeRouteResponse.setValue(response.body());
                        } else {
                            removeRouteResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        removeRouteResponse.setValue(null);
                    }
                });
        return removeRouteResponse;
    }
}
