package com.example.drogopolex.data.repositories;

import android.util.Log;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.AddEventRequest;
import com.example.drogopolex.data.network.request.EventsByGpsRequest;
import com.example.drogopolex.data.network.request.FilterEventsRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.ResponseType;
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

    public LiveData<BasicResponse> addEvent(String localization, String eventType, String userId, String token) {
        final MutableLiveData<BasicResponse> addEventResponse = new MutableLiveData<>();

        myApi.eventsAddEvent(token, userId,
                new AddEventRequest(
                    false,
                    localization,
                    "",
                    "",
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

    public LiveData<BasicResponse> addEventByGps(LocationDetails locationDetails, String eventType, String userId, String token) {
        final MutableLiveData<BasicResponse> addEventResponse = new MutableLiveData<>();

        myApi.eventsAddEvent(token, userId,
                new AddEventRequest(
                true,
                "",
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

    public LiveData<EventsResponse> getEventsByType(String userId, String token, String type) {
        final MutableLiveData<EventsResponse> getEventsByTypeResponse = new MutableLiveData<>();

        myApi.eventsGetEvents(userId, token, new FilterEventsRequest("", type)).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if(response.isSuccessful()){
                    getEventsByTypeResponse.setValue(response.body());
                } else {
                    getEventsByTypeResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                getEventsByTypeResponse.setValue(null);
            }
        });
        return getEventsByTypeResponse;
    }

    public LiveData<EventsResponse> getEventsByLocalization(String userId, String token, String localization) {
        final MutableLiveData<EventsResponse> getEventsByLocalizationResponse = new MutableLiveData<>();

        myApi.eventsGetEvents(userId, token ,new FilterEventsRequest(localization, "")).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if(response.isSuccessful()){
                    getEventsByLocalizationResponse.setValue(response.body());
                } else {
                    getEventsByLocalizationResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                getEventsByLocalizationResponse.setValue(null);
            }
        });
        return getEventsByLocalizationResponse;
    }

    public LiveData<EventsResponse> getEventsByTypeAndLocalization(String userId, String token, String type, String localization) {
        final MutableLiveData<EventsResponse> getEventsByTypeAndLocalizationResponse = new MutableLiveData<>();

        myApi.eventsGetEvents(userId, token, new FilterEventsRequest(localization, type)).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if(response.isSuccessful()){
                    getEventsByTypeAndLocalizationResponse.setValue(response.body());
                } else {
                    getEventsByTypeAndLocalizationResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                getEventsByTypeAndLocalizationResponse.setValue(null);
            }
        });
        return getEventsByTypeAndLocalizationResponse;
    }
}
