package com.example.drogopolex.subscription.activities;

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
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.auth.activities.ProfileActivity;
import com.example.drogopolex.auth.viewModel.ProfileViewModel;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.subscription.viewModel.SubscribeViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class SubscribeActivity extends AppCompatActivity {


    /*Button goToLoggedInMenuActivity;
    Button subscribe;
    EditText localizationInput;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySubscribeBinding activitySubscribeBinding;
        activitySubscribeBinding = DataBindingUtil.setContentView(this,R.layout.activity_subcribe);

        activitySubscribeBinding.setViewModel(new SubscribeViewModel());
        activitySubscribeBinding.executePendingBindings();
        activitySubscribeBinding.getViewModel().sharedPreferencesHolder = this;
        activitySubscribeBinding.getViewModel().basicListener = this;

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

        //chyba niepotrzebne
        //SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        //if(!sp.getBoolean("loggedIn", false)){
           // goToMainActivity();
        //}
    }
    @Override
    public void onSuccess(LiveData<BasicResponse> response) {
        response.observe(this, new Observer<BasicResponse>() {
            @Override
            public void onChanged(BasicResponse result) {
                if(response.getValue() != null) {
                    if ("true".equals(response.getValue().getSuccess())) {
                        Toast.makeText(SubscribeActivity.this, "Operacja powiodła się.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SubscribeActivity.this, response.getValue().getErrorString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SubscribeActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }

    private void goToLoggedInMenuActivity() {
        Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
        startActivity(goToLoggedInMenuActivityIntent);
    }
/*
    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
        startActivity(goToMainActivityIntent);
    }

    private void addEventRequest() {
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        String localization = localizationInput.getText().toString();
        String coordinates = "(200, 300)"; //todo hardcoded value

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
    }*/
}