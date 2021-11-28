package com.example.drogopolex.data.network;

import com.example.drogopolex.data.network.request.AddEventRequest;
import com.example.drogopolex.data.network.request.AddVoteRequest;
import com.example.drogopolex.data.network.request.ChangeUserDataRequest;
import com.example.drogopolex.data.network.request.ChangeVoteRequest;
import com.example.drogopolex.data.network.request.GenerateRouteRequest;
import com.example.drogopolex.data.network.request.GetEventsRequest;
import com.example.drogopolex.data.network.request.LoginRequest;
import com.example.drogopolex.data.network.request.RegisterRequest;
import com.example.drogopolex.data.network.request.RemoveVoteRequest;
import com.example.drogopolex.data.network.request.SubscribeRequest;
import com.example.drogopolex.data.network.request.UnsubscribeRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.network.response.ProfileResponse;
import com.example.drogopolex.data.network.response.RouteValue;
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
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId);

    @POST("changeUserData")
    Call<BasicResponse> userChangeUserData(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId,
            @Body ChangeUserDataRequest body);

    @POST("myProfile")
    Call<ProfileResponse> userGetUserData(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId);

    /*
     * Events Requests
     */

    @POST("getEvents")
    Call<EventsResponse> eventsGetEvents(
            @Header("User-Id") String userId,
            @Header("Authorization-Token") String token,
            @Body GetEventsRequest body);

    @POST("addEvent")
    Call<BasicResponse> eventsAddEvent(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId,
            @Body AddEventRequest body);

    @POST("generateRoute")
    Call<RouteValue> eventsGenerateRoute(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId,
            @Body GenerateRouteRequest body);
    /*
     * Subscription Requests
     */

    @POST("subscribe")
    Call<BasicResponse> subscriptionSubscribe(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId,
            @Body SubscribeRequest body);

    @POST("subscriptions")

    Call<SubscriptionsResponse> subscriptionSubscriptions(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId);

    @POST("unsubscribe")
    Call<BasicResponse> subscriptionUnsubscribe(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId,
            @Body UnsubscribeRequest body);

    /*
     * Votes Requests
     */

    @POST("vote")
    Call<BasicResponse> votesAddVote(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId,
            @Body AddVoteRequest body
        );

    @POST("change_vote")
    Call<BasicResponse> votesChangeVote(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId,
            @Body ChangeVoteRequest body);

    @POST("remove_vote")
    Call<BasicResponse> votesRemoveVote(
            @Header("Authorization-Token") String token,
            @Header("User-Id") String userId,
            @Body RemoveVoteRequest body);
}
