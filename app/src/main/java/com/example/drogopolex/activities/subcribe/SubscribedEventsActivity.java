package com.example.drogopolex.activities.subcribe;

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
import com.example.drogopolex.ServerUtils;
import com.example.drogopolex.activities.main.LoggedInMenuActivity;
import com.example.drogopolex.activities.main.MainActivity;
import com.example.drogopolex.adapters.EventListAdapter;
import com.example.drogopolex.model.DrogopolexEvent;
import com.example.drogopolex.model.Vote;
import com.example.drogopolex.model.VoteType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribedEventsActivity extends AppCompatActivity {
    Button goToLoggedInMenuActivityButton;
    Button addSubscriptionButton;
    Button goToSubscriptionListButton;
    RecyclerView subscribedEventsRecyclerView;

    EventListAdapter eventListAdapter;
    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();
    ArrayList<String> subscriptions = new ArrayList<>();
    boolean subscriptionsUpdatedFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_events);

        goToLoggedInMenuActivityButton = (Button) findViewById(R.id.go_back_subscribed_events);
        addSubscriptionButton = (Button) findViewById(R.id.addSubscription);
        goToSubscriptionListButton = (Button) findViewById(R.id.go_to_subscriptions_list);

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

        goToSubscriptionListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSubscriptionsListActivity();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }


        eventListAdapter = new EventListAdapter(eventListData, this);
        subscribedEventsRecyclerView.setAdapter(eventListAdapter);
        subscribedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new getEventsFromSubscribed().execute();
    }

    private void getSubscriptions(CountDownLatch latch) {
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
                            subscriptions.add(localization_str);
                        }
                        subscriptionsUpdatedFlag = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
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

    private void getAllEventsRequest() { //todo: Ta funkcja jest duplikatem.
        //Toast.makeText(this, "getAllevents", Toast.LENGTH_SHORT).show();
        CountDownLatch latch = new CountDownLatch(2);
        List<Vote> votes = ServerUtils.getVotes(this, latch);
        getSubscriptions(latch);
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        int userId = Integer.parseInt(sp.getString("user_id", ""));
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
                            String street = item.getString("street");
                            int eventId = Integer.parseInt(item.getString("id"));
                            List<Vote> eventVotes = votes.stream()
                                    .filter(vote -> vote.getEventId() == eventId)
                                    .collect(Collectors.toList());
                            VoteType userVoteType = eventVotes.stream()
                                    .filter(vote -> vote.getUserId() == userId)
                                    .findFirst()
                                    .map(Vote::getType)
                                    .orElse(VoteType.NO_VOTE);
                            if(subscriptions.stream().anyMatch(sub -> sub.trim().equals(localization_str))) {
                                eventListData.add(new DrogopolexEvent(type_str, localization_str, street, eventId, eventVotes, userVoteType));
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

            latch.await(1000, TimeUnit.MILLISECONDS);
            RequestSingleton.getInstance(this).addToRequestQueue(objectRequest);

        } catch (JSONException | InterruptedException e) {
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

    private void goToSubscriptionsListActivity() {
        Intent goToSubscriptionsListActivityIntent = new Intent(this, SubscriptionsActivity.class);
        startActivity(goToSubscriptionsListActivityIntent);
    }

    private class getEventsFromSubscribed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getAllEventsRequest();
        }
    }
}