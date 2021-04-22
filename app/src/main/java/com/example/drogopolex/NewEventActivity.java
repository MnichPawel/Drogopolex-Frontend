package com.example.drogopolex;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONException;
import org.json.JSONObject;

public class NewEventActivity extends AppCompatActivity {
    Button goToLoggedInMenuActivity;
    Button addEvent;
    EditText localizationInput;
    EditText eventTypeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_new_event);
        addEvent = (Button) findViewById(R.id.addNewEventButton);
        localizationInput = (EditText) findViewById(R.id.editTextLocalization);
        eventTypeInput = (EditText) findViewById(R.id.editTextEventType);

        goToLoggedInMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventRequest();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
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

    private void addEventRequest() {
        String localization = localizationInput.getText().toString();
        String eventType = eventTypeInput.getText().toString();
        String coordinates = "(200, 300)";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("localization", localization);
            jsonObject.put("coordinates", coordinates);
            jsonObject.put("type", eventType);

            String url = "http://10.0.2.2:5000/addEvent";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Boolean isSuccess = false;
                    String stringError = "";

                    try {
                        isSuccess = response.getBoolean("success");
                        if(!isSuccess) {
                            stringError = response.getString("errorString");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (isSuccess) {
                        Toast.makeText(getApplicationContext(), "Zgłoszenie zostało dodane", Toast.LENGTH_LONG).show();
                        goToLoggedInMenuActivity();
                    } else {
                        Toast.makeText(getApplicationContext(), stringError, Toast.LENGTH_LONG).show();
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
}