package com.example.drogopolex.data.network;

import com.example.drogopolex.data.network.request.AddEventRequest;
import com.example.drogopolex.data.network.request.ChangeUserDataRequest;
import com.example.drogopolex.data.network.request.EventsByGpsRequest;
import com.example.drogopolex.data.network.request.FilterEventsRequest;
import com.example.drogopolex.data.network.request.LoginRequest;
import com.example.drogopolex.data.network.request.RegisterRequest;
import com.example.drogopolex.data.network.request.SubscribeRequest;

import com.example.drogopolex.data.network.request.SubscriptionEventsRequest;
import com.example.drogopolex.data.network.request.SubscriptionsRequest;

import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.network.response.ProfileResponse;
import com.example.drogopolex.data.network.response.SubscriptionsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MyApi {

    /*
     * User Data Requests
     */

    @POST("login")
    Call<LoginResponse> userLogin(
            @Body LoginRequest body);

    @POST("register")
    Call<BasicResponse> userRegister(
            @Body RegisterRequest body);

    @POST("logout")
    Call<BasicResponse> userLogout(
            @Header("AuthorizationToken") String token,
            @Header("UserId") String userId);

    @POST("changeUserData")
    Call<BasicResponse> userChangeUserData(
            @Header("AuthorizationToken") String token,
            @Header("UserId") String userId,
            @Body ChangeUserDataRequest body);

    @POST("myProfile")
    Call<ProfileResponse> userGetUserData(
            @Header("AuthorizationToken") String token,
            @Header("UserId") String userId);

    /*
     * Events Requests
     */

    @POST("getEventsFromUserArea")
    Call<EventsResponse> eventsGetFromUserArea(
            @Header("UserId") String userId,
            @Header("AuthorizationToken") String token,
            @Body EventsByGpsRequest body);

    @POST("addEvent")
    Call<BasicResponse> eventsAddEvent(
            @Header("AuthorizationToken") String token,
            @Header("UserId") String userId,
            @Body AddEventRequest body);

    @POST("getEventsByLocalization")
    Call<EventsResponse> eventsGetEventsByLocalization(
            @Header("UserId") String userId,
            @Header("AuthorizationToken") String token,
            @Body FilterEventsRequest body);

    @POST("getEventsByType")
    Call<EventsResponse> eventsGetEventsByType(
            @Header("UserId") String userId,
            @Header("AuthorizationToken") String token,
            @Body FilterEventsRequest body);

    @POST("getEventsByTypeAndLoc")
    Call<EventsResponse> eventsGetEventsByTypeAndLocalization(
            @Header("UserId") String userId,
            @Header("AuthorizationToken") String token,
            @Body FilterEventsRequest body);

    /*
     * Subscription Requests
     */

    @POST("subscribe")
    Call<BasicResponse> subscriptionSubscribe(
            @Header("AuthorizationToken") String token,
            @Header("UserId") String userId,
            @Body SubscribeRequest body);

    @POST("subscriptions")

    Call<SubscriptionsResponse> subscriptionSubscriptions(
            @Header("AuthorizationToken") String token,
            @Header("UserId") String userId,
            @Body SubscriptionsRequest body);
    //Call<SubscriptionsResponse> subscriptionSubscriptions(@Body SubscriptionsRequest body);

    @POST("subscriptionsEvents")
    Call<EventsResponse> subscriptionEvents(
            @Header("AuthorizationToken") String token,
            @Header("UserId") String userId,
            @Body SubscriptionEventsRequest body);

}
