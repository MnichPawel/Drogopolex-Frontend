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

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class SubscribeActivity extends AppCompatActivity {
    Button goToLoggedInMenuActivity;
    Button subscribe;
    EditText localizationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcribe);

        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_subscribe);
        subscribe = (Button) findViewById(R.id.subscribeButton);
        localizationInput = (EditText) findViewById(R.id.editTextLocalization2);

        goToLoggedInMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
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
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        String localization = localizationInput.getText().toString();
        String coordinates = "(200, 300)";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("localization", localization);
            jsonObject.put("coordinates", coordinates);
            jsonObject.put("user_id", user_id);
            jsonObject.put("token", token);

            String url = "http://10.0.2.2:5000/subscribe";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    boolean isSuccess = false;
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
                        Toast.makeText(getApplicationContext(), "Zasubskrybowano lokalizację", Toast.LENGTH_LONG).show();
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