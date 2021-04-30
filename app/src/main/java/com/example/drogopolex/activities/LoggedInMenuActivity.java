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

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class LoggedInMenuActivity extends AppCompatActivity {
    Button goToNewEventActivity;
    Button goToEventsActivity;
    Button goToSubscriptionsActivity;
    Button goToProfileActivity;

    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_menu);

        goToNewEventActivity = (Button) findViewById(R.id.add_event_button);
        goToEventsActivity = (Button) findViewById(R.id.events_button);
        goToSubscriptionsActivity = (Button) findViewById(R.id.subscriptions_button);
        goToProfileActivity = (Button) findViewById(R.id.profile_button);

        logoutButton = (Button) findViewById(R.id.logout_button);

        goToNewEventActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNewEventActivity();
            }
        });

        goToEventsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEventsActivity();
            }
        });

        goToSubscriptionsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSubscriptionsActivity();
            }
        });

        goToProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfileActivity();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }
    }

    private void goToNewEventActivity() {
        Intent goToNewEventActivityIntent = new Intent(this, NewEventActivity.class);
        startActivity(goToNewEventActivityIntent);
    }

    private void goToEventsActivity() {
        Intent goToEventsActivityIntent = new Intent(this, EventsActivity.class);
        startActivity(goToEventsActivityIntent);
    }

    private void goToSubscriptionsActivity() {
        Intent goToSubscriptionsActivityIntent = new Intent(this, SubscribedEventsActivity.class);
        startActivity(goToSubscriptionsActivityIntent);
    }

    private void goToProfileActivity() {
        Intent goToProfileActivityIntent = new Intent(this, ProfileActivity.class);
        startActivity(goToProfileActivityIntent);
    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    private void logout() {
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        boolean loggedIn = sp.getBoolean("loggedIn", false);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        if(!loggedIn){
            return;
        }

        try {
            //wrzucenie podanych danych do jsona
            JSONObject jo = new JSONObject();
            jo.put("user_id",user_id);
            jo.put("token",token);

            String url= "http://10.0.2.2:5000/logout";
            JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, url, jo, new Response.Listener<JSONObject>() {
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

                    if(isSuccess){
                        Toast.makeText(getApplicationContext(),"Wylogowano",Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor speditor = sp.edit();
                        speditor.putString("token","");
                        speditor.putString("user_id","");
                        speditor.putBoolean("loggedIn",false);
                        speditor.apply();

                        goToMainActivity();
                    }else{
                        Toast.makeText(getApplicationContext(),stringError,Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor speditor = sp.edit();
                        speditor.putString("token","");
                        speditor.putString("user_id","");
                        speditor.putBoolean("loggedIn",false);
                        speditor.apply();

                        goToMainActivity();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            });

            RequestSingleton.getInstance(this).addToRequestQueue(sr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}