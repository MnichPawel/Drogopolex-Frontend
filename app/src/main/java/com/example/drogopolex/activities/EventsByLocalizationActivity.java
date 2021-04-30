package com.example.drogopolex.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class EventsByLocalizationActivity extends AppCompatActivity {
    Button goToLoggedInMenuActivity;
    Button searchEventsByLocalizationButton;
    EditText searchEventsByLocalizationInput;

    EventListAdapter eventListAdapter;
    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_by_localization);

        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_events_by_localization);
        searchEventsByLocalizationButton = (Button) findViewById(R.id.search_events_by_localization_button);
        searchEventsByLocalizationInput = (EditText) findViewById(R.id.searchEventsByLocalizationInput);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.eventsByLocalizationListView);

        eventListAdapter = new EventListAdapter(eventListData);
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        goToLoggedInMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });

        searchEventsByLocalizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEventsByLocalizationRequest();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }
    }

    private void getEventsByLocalizationRequest() {
        String localization = searchEventsByLocalizationInput.getText().toString();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("localization", localization);
            jsonObject.put("token", ""); //Na przyszłość jak będzie potrzebne

            String url = "http://10.0.2.2:5000/getEventsByLocalization";
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
                            eventListData.add(new DrogopolexEvent(type_str, localization_str));
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
}