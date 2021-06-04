package com.example.drogopolex.activities;

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
import com.example.drogopolex.adapters.SubscriptionsListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionsActivity extends AppCompatActivity {
    Button goToSubscribedEventsButton;
    RecyclerView subscriptionsRecyclerView;

    SubscriptionsListAdapter listAdapter;

    ArrayList<String> subscriptions = new ArrayList<>();
    ArrayList<String> subscriptionIds = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);


        goToSubscribedEventsButton = (Button) findViewById(R.id.go_back_subscriptions);
        subscriptionsRecyclerView = (RecyclerView) findViewById(R.id.subscriptions_view);

        goToSubscribedEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSubscribedEventsActivity();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }

        listAdapter = new SubscriptionsListAdapter(subscriptions,subscriptionIds,getApplicationContext(),sp.getString("user_id", ""));
        subscriptionsRecyclerView.setAdapter(listAdapter);
        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSubscriptions();
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

    }

    private void goToSubscribedEventsActivity(){ //TODO: czy da sie te wszystkie goto wsadzic do jednej klasy utilsowej?
        Intent goToSubscribedEventsActivityIntent = new Intent(this, SubscribedEventsActivity.class);
        startActivity(goToSubscribedEventsActivityIntent);
    }


    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }
}
