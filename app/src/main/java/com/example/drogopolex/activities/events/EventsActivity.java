package com.example.drogopolex.activities.events;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.example.drogopolex.adapters.EventListAdapter;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EventsActivity extends AppCompatActivity implements LocationListener {
    Button goToLoggedInMenuActivity;
    Button goToEventsByLocalizationActivity; //now it goes to EventsSearchActivity,  EventsByLocalizationActivity deprecated

    EventListAdapter eventListAdapter;
    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    LocationManager locationManager;
    private final int PERMISSIONS_REQUEST_LOCATION = 99;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_events);
        goToEventsByLocalizationActivity = (Button) findViewById(R.id.goToEventsByLocalizationActivity);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.eventsListView);

        eventListAdapter = new EventListAdapter(eventListData, this);
        recyclerView.setAdapter(eventListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSIONS_REQUEST_LOCATION);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50, this);


        goToLoggedInMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });

        goToEventsByLocalizationActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goToEventsByLocalizationActivity();
                goToEventsSearchActivity();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }
    }

    private void getEventsFromUserAreaRequest() {
        CountDownLatch latch = new CountDownLatch(1);
        List<Vote> votes = ServerUtils.getVotes(this, latch);
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        int userId = Integer.parseInt(sp.getString("user_id", ""));
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("longitude", longitude);
            jsonObject.put("latitude", latitude);

            String url = "http://10.0.2.2:5000/getEventsFromUserArea";
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
                            eventListData.add(new DrogopolexEvent(type_str, localization_str, street, eventId, eventVotes, userVoteType));
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

    private void goToEventsSearchActivity() {
        Intent goToEventsSearchActivityIntent = new Intent(this, EventsSearchActivity.class);
        startActivity(goToEventsSearchActivityIntent);
    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
        startActivity(goToMainActivityIntent);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        getEventsFromUserAreaRequest();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "PERMISSION GRANTED", Toast.LENGTH_LONG).show();
            }
        }
    }
}
