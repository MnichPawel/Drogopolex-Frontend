package com.example.drogopolex.events.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.drogopolex.constants.EventTypes;
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

public class EventsSearchActivity extends AppCompatActivity {
    Button goToLoggedInMenuActivity;
    Button searchEventsByLocalizationButton;
    EditText searchEventsByLocalizationInput;
    Spinner spinnerEvTyp;
    String wybranaAktywnosc;

    List<String> listaZdarzen = EventTypes.getEventTypes();

    EventListAdapter eventListAdapter;
    ArrayList<DrogopolexEvent> eventListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_search);
        //Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
        //listaZdarzen.add(0,"Dowolny typ");

        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_events_search);
        searchEventsByLocalizationButton = (Button) findViewById(R.id.search_events_button);
        searchEventsByLocalizationInput = (EditText) findViewById(R.id.searchEventsSearchLocalizationInput);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.eventsSearchListView);
        //Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
        eventListAdapter = new EventListAdapter(eventListData, this);
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
                getEventsRequest();
            }
        });
        spinnerEvTyp= (Spinner) findViewById(R.id.spinnerEventSearch); //lista wyskakujaca
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaZdarzen);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerEvTyp.setAdapter(dataAdapter);
        spinnerEvTyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wybranaAktywnosc = listaZdarzen.get(i); //jak uzytkownik cos wybral to to ustaw
                //tutaj poinformuj ze sie cale te, ale to chyba nie jest wymagane
                //getEventsRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                wybranaAktywnosc=""; //jak nie to nic nie ustawiaj
            }
        });
        //Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }
    }

    private void getEventsRequest() {
        CountDownLatch latch = new CountDownLatch(1);
        List<Vote> votes = ServerUtils.getVotes(this, latch);
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        int userId = Integer.parseInt(sp.getString("user_id", ""));
        String localization = searchEventsByLocalizationInput.getText().toString();
        try {
            JSONObject jsonObject = new JSONObject();
            String url ="";
            if(listaZdarzen.get(0).equals(wybranaAktywnosc) || "".equals(wybranaAktywnosc)) { //wybrano tylko lokalizacje
                jsonObject.put("localization", localization);
                jsonObject.put("token", ""); //Na przyszłość jak będzie potrzebne

                 url = "http://10.0.2.2:5000/getEventsByLocalization";
            }
            else{ //wybrano co najmniej typ
                if("".equals(localization)){ //jesli wybrano wylacznie typ

                    jsonObject.put("type", wybranaAktywnosc);
                    jsonObject.put("token", ""); //Na przyszłość jak będzie potrzebne

                    url = "http://10.0.2.2:5000/getEventsByType";
                }
                else{ //zarowno po typie jak i po lokalizacyi
                    jsonObject.put("localization", localization);
                    jsonObject.put("type", wybranaAktywnosc);
                    jsonObject.put("token", ""); //Na przyszłość jak będzie potrzebne
                    url = "http://10.0.2.2:5000/getEventsByTypeAndLoc";
                }

            }
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

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
        startActivity(goToMainActivityIntent);
    }
}
