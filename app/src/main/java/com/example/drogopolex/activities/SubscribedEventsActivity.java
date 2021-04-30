package com.example.drogopolex.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.example.drogopolex.model.DrogopolexEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribedEventsActivity extends AppCompatActivity {
    Button goToLoggedInMenuActivityButton;
    Button addSubscriptionButton;
    RecyclerView subscribedEventsRecyclerView;

    EventListAdapter eventListAdapter;
    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();
    ArrayList<String> subscriptions = new ArrayList<>();
    boolean subscriptionsUpdatedFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        goToLoggedInMenuActivityButton = (Button) findViewById(R.id.go_back_subscriptions);
        addSubscriptionButton = (Button) findViewById(R.id.addSubscription);

        subscribedEventsRecyclerView = (RecyclerView) findViewById(R.id.subscribed_events_view);

        goToLoggedInMenuActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });

        addSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSubscribeActivity();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }


        eventListAdapter = new EventListAdapter(eventListData);
        subscribedEventsRecyclerView.setAdapter(eventListAdapter);
        subscribedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new getEventsFromSubscribed().execute();
        //getSubscriptions();
        //while(!subscriptionsUpdatedFlag);
        //getAllEventsRequest();
    }

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
                        subscriptionsUpdatedFlag = false;
                        for (int i = 0; i < resp.length(); i++) {
                            JSONObject item = resp.getJSONObject(i);
                            String localization_str = item.getString("localization");
                            Toast.makeText(getApplicationContext(), localization_str, Toast.LENGTH_LONG).show();
                            subscriptions.add(localization_str);
                        }
                        subscriptionsUpdatedFlag = true;
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

    }

    private void getAllEventsRequest() { //todo: to bardziej workaround niż docelowe rozwiązanie. Ta funkcja jest duplikatem.
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", "");

            String url = "http://10.0.2.2:5000/getAllEvents";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray resp = response.getJSONArray("events");
                        eventListData.clear();
                        for (int i = 0; i < resp.length(); i++) {
                            JSONObject item = resp.getJSONObject(i);
                            String type_str = item.getString("type");
                            String localization_str = item.getString("localization");
                            if(subscriptions.stream().anyMatch(sub -> sub.trim().equals(localization_str))) {
                                eventListData.add(new DrogopolexEvent(type_str, localization_str));
                            }
                        }
                        eventListAdapter.notifyDataSetChanged();

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
    }


    private void goToLoggedInMenuActivity() {
        Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
        startActivity(goToLoggedInMenuActivityIntent);
    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    private void goToSubscribeActivity() {
        Intent goToSubscribeActivityIntent = new Intent(this, SubscribeActivity.class);
        startActivity(goToSubscribeActivityIntent);
    }

    private class getEventsFromSubscribed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getSubscriptions();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getAllEventsRequest();
        }
    }
}