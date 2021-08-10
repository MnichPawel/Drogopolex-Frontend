package com.example.drogopolex.subscription.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.drogopolex.R;
import com.example.drogopolex.RequestSingleton;
import com.example.drogopolex.adapters.EventListAdapter;
import com.example.drogopolex.adapters.SubscriptionsListAdapter;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.data.network.response.EventsResponse;
import com.example.drogopolex.data.network.response.SubscriptionsResponse;
import com.example.drogopolex.databinding.ActivityEventsBinding;
import com.example.drogopolex.databinding.ActivitySubscriptionsBinding;
import com.example.drogopolex.events.activities.EventsActivity;
import com.example.drogopolex.events.activities.EventsSearchActivity;
import com.example.drogopolex.events.utils.EventsAction;
import com.example.drogopolex.events.viewModel.EventsViewModel;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.DrogopolexSubscription;
import com.example.drogopolex.model.Vote;
import com.example.drogopolex.model.VoteType;
import com.example.drogopolex.subscription.utils.SubscriptionsAction;
import com.example.drogopolex.subscription.viewModel.SubscriptionsViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsActivity extends AppCompatActivity implements OnSuccessListener<LiveData<SubscriptionsResponse>> {
    //Button goToSubscribedEventsButton;
    RecyclerView subscriptionsRecyclerView;
    ActivitySubscriptionsBinding activitySubscriptionsBinding;
    SubscriptionsListAdapter listAdapter;

    ArrayList<DrogopolexSubscription> subscriptions = new ArrayList<>();
   // ArrayList<String> subscriptionIds = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySubscriptionsBinding = DataBindingUtil.setContentView(this, R.layout.activity_subscriptions);
        activitySubscriptionsBinding.setViewModel(new SubscriptionsViewModel(getApplication()));
        activitySubscriptionsBinding.executePendingBindings();
        activitySubscriptionsBinding.getViewModel().onSuccessListener = this;

        activitySubscriptionsBinding.getViewModel().getAction().observe(this, new Observer<SubscriptionsAction>() {
            @Override
            public void onChanged(SubscriptionsAction subscriptionsAction) {
                if(subscriptionsAction != null){
                    handleAction(subscriptionsAction);
                }
            }
        });
        //goToSubscribedEventsButton = (Button) findViewById(R.id.go_back_subscriptions);
        subscriptionsRecyclerView = (RecyclerView) findViewById(R.id.subscriptionsView);

        /*goToSubscribedEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSubscribedEventsActivity();
            }
        });*/

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }

        //listAdapter = new SubscriptionsListAdapter(subscriptions,subscriptionIds,getApplicationContext());
        //subscriptionsRecyclerView=  (RecyclerView) findViewById(R.id.subscriptionsView);
        //subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

       // getSubscriptions();
    }

    private void handleAction(SubscriptionsAction subscriptionsAction) {
        switch (subscriptionsAction.getValue()) {
            case SubscriptionsAction.SHOW_LOGGED_IN:
                Intent goToLoggedInIntent = new Intent(this, LoggedInMenuActivity.class);
                startActivity(goToLoggedInIntent);
                break;
            case SubscriptionsAction.SHOW_SUBSCRIBED_EVENTS:
                Intent goToSubscribedEventsIntent = new Intent(this, SubscribedEventsActivity.class);
                startActivity(goToSubscribedEventsIntent);
        }
    }

    @Override
    public void onSuccess(LiveData<SubscriptionsResponse> subscriptionsResponseLiveData) {
        subscriptionsResponseLiveData.observe(this, new Observer<SubscriptionsResponse>() {
            @Override
            public void onChanged(SubscriptionsResponse subscriptionsResponse) {
                if(subscriptionsResponse != null) {
                    subscriptions.clear();
                    subscriptionsResponse.getSubscriptions()
                            .forEach(subscription -> {
                                subscriptions.add(new DrogopolexSubscription(
                                        Integer.parseInt(subscription.getId()),
                                        subscription.getLocalization()

                                ));
                            });
                    if(listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    } else {
                        listAdapter = new SubscriptionsListAdapter(subscriptions, SubscriptionsActivity.this);
                        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(SubscriptionsActivity.this));
                        subscriptionsRecyclerView.setAdapter(listAdapter);
                    }
                } else {
                    Toast.makeText(SubscriptionsActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //stare
    /*
    private void getSubscriptions() {
        JSONObject jsonObject = new JSONObject();
        String url = "http://10.0.2.2:5000/subscriptions";

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");

        try {
            jsonObject.put("token", "");
            jsonObject.put("user_id", user_id);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray resp = response.getJSONArray("subscriptions");
                        subscriptions.clear();
                        for (int i = 0; i < resp.length(); i++) {
                            JSONObject item = resp.getJSONObject(i);
                            String localization_str = item.getString("localization");
                            subscriptions.add(localization_str);
                            String sub_id_str = item.getString("id_sub");
                            subscriptionIds.add(sub_id_str);
                        }
                        listAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            });

            RequestSingleton.getInstance(this).addToRequestQueue(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    private void goToSubscribedEventsActivity(){ //TODO: czy da sie te wszystkie goto wsadzic do jednej klasy utilsowej?
        Intent goToSubscribedEventsActivityIntent = new Intent(this, SubscribedEventsActivity.class);
        startActivity(goToSubscribedEventsActivityIntent);
    }


    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
        startActivity(goToMainActivityIntent);
    }
}
