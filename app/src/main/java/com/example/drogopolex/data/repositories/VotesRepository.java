package com.example.drogopolex.data.repositories;

import com.example.drogopolex.data.network.MyApi;
import com.example.drogopolex.data.network.request.AddVoteRequest;
import com.example.drogopolex.data.network.request.ChangeVoteRequest;
import com.example.drogopolex.data.network.request.RemoveVoteRequest;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.ResponseType;
import com.example.drogopolex.data.network.utils.ErrorUtils;
import com.example.drogopolex.data.network.utils.RetrofitUtils;
import com.example.drogopolex.model.VoteType;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VotesRepository {
    private MyApi myApi;

    public VotesRepository(){
        myApi =  RetrofitUtils.getRetrofitInstance().create(MyApi.class);
    }

    public LiveData<BasicResponse> votesAddVote(String user_id, String token, VoteType type, String eventId) {
        final MutableLiveData<BasicResponse> addVoteResponse = new MutableLiveData<>();

        myApi.votesAddVote(token, user_id, new AddVoteRequest(eventId, type.getValue()))
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if(response.isSuccessful()){
                            addVoteResponse.setValue(response.body());
                        } else {
                            addVoteResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        addVoteResponse.setValue(null);
                    }
                });

        return addVoteResponse;
    }

    public LiveData<BasicResponse> votesChangeVote(String user_id, String token, VoteType type, String eventId) {
        final MutableLiveData<BasicResponse> changeVoteResponse = new MutableLiveData<>();

        myApi.votesChangeVote(token, user_id, new ChangeVoteRequest(eventId, type.getValue()))
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if(response.isSuccessful()){
                            changeVoteResponse.setValue(response.body());
                        } else {
                            changeVoteResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        changeVoteResponse.setValue(null);
                    }
                });

        return changeVoteResponse;
    }

    public LiveData<BasicResponse> votesRemoveVote(String user_id, String token, String eventId) {
        final MutableLiveData<BasicResponse> removeVoteResponse = new MutableLiveData<>();

        myApi.votesRemoveVote(token, user_id, new RemoveVoteRequest(eventId))
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        if(response.isSuccessful()){
                            removeVoteResponse.setValue(response.body());
                        } else {
                            removeVoteResponse.setValue((BasicResponse) ErrorUtils.parseErrorResponse(response, ResponseType.BASIC_RESPONSE));
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        removeVoteResponse.setValue(null);
                    }
                });

        return removeVoteResponse;
    }
}