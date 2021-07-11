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
import com.example.drogopolex.activities.main.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    Button goToLoggedInMenuActivity;

    EditText usernameInput;
    Button changeUsernameButton;

    EditText emailInput;
    Button changeEmailButton;

    EditText passwordInput;
    EditText passwordRepeatInput;
    Button changePasswordButton;

    String old_username;
    String old_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_profile);

        usernameInput = (EditText) findViewById(R.id.profile_change_username_textfield);
        changeUsernameButton = (Button) findViewById(R.id.profile_change_username_button);
        changeUsernameButton.setEnabled(false);

        emailInput = (EditText) findViewById(R.id.profile_change_email_textfield);
        changeEmailButton = (Button) findViewById(R.id.profile_change_email_button);
        changeEmailButton.setEnabled(false);

        passwordInput = (EditText) findViewById(R.id.profile_change_password_textfield);
        passwordRepeatInput = (EditText) findViewById(R.id.profile_change_password_repeat_textfield);
        changePasswordButton = (Button) findViewById(R.id.profile_change_password_button);
        changePasswordButton.setEnabled(false);

        goToLoggedInMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });


        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUsernameRequest();
            }
        });

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmailRequest();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordRequest();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }

        getCurrentProfileRequest();
    }

    private void getCurrentProfileRequest() {
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        boolean loggedIn = sp.getBoolean("loggedIn", false);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        if(!loggedIn){
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", token); //Na przyszłość jak będzie potrzebne
            jsonObject.put("user_id", user_id);

            String url = "http://10.0.2.2:5000/myProfile";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String ret_username = response.getString("nazwa");
                        String ret_email = response.getString("email");

                        usernameInput.setText(ret_username);
                        emailInput.setText(ret_email);

                        old_username = ret_username;
                        old_email = ret_email;

                        changeUsernameButton.setEnabled(true);
                        changeEmailButton.setEnabled(true);
                        changePasswordButton.setEnabled(true);

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

    private void changeUsernameRequest() {
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        boolean loggedIn = sp.getBoolean("loggedIn", false);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        if(!loggedIn){
            return;
        }

        String new_username = usernameInput.getText().toString();

        if(new_username.equals(old_username)){
            Toast.makeText(this.getApplicationContext(), "Nie zmieniłeś nazwy użytkownika",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            JSONObject jo = new JSONObject();
            jo.put("user_id",user_id);
            jo.put("token",token);
            jo.put("changed_value","username");
            jo.put("new_value", new_username);

            String url= "http://10.0.2.2:5000/changeUserData";
            JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, url, jo, new Response.Listener<JSONObject>() {
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

                    if(isSuccess){
                        Toast.makeText(getApplicationContext(),"Zmieniono nazwę użytkownika",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),stringError,Toast.LENGTH_LONG).show();
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

    private void changeEmailRequest() {
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        boolean loggedIn = sp.getBoolean("loggedIn", false);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        if(!loggedIn){
            return;
        }

        String new_email = emailInput.getText().toString();

        if(new_email.equals(old_email)){
            Toast.makeText(this.getApplicationContext(), "Nie zmieniłeś adresu email",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            JSONObject jo = new JSONObject();
            jo.put("user_id",user_id);
            jo.put("token",token);
            jo.put("changed_value","email");
            jo.put("new_value", new_email);

            String url= "http://10.0.2.2:5000/changeUserData";
            JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, url, jo, new Response.Listener<JSONObject>() {
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

                    if(isSuccess){
                        Toast.makeText(getApplicationContext(),"Zmieniono email",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),stringError,Toast.LENGTH_LONG).show();
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

    private void changePasswordRequest() {
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        boolean loggedIn = sp.getBoolean("loggedIn", false);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        if(!loggedIn){
            return;
        }

        String new_pass1 = passwordInput.getText().toString();
        String new_pass2 = passwordRepeatInput.getText().toString();

        if(!new_pass1.equals(new_pass2)){
            Toast.makeText(this.getApplicationContext(), "Hasła nie są takie same",Toast.LENGTH_LONG).show();
            return;
        }

        try {
            JSONObject jo = new JSONObject();
            jo.put("user_id",user_id);
            jo.put("token",token);
            jo.put("changed_value","password");
            jo.put("new_value", new_pass1);

            String url= "http://10.0.2.2:5000/changeUserData";
            JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, url, jo, new Response.Listener<JSONObject>() {
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

                    if(isSuccess){
                        Toast.makeText(getApplicationContext(),"Zmieniono hasło",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),stringError,Toast.LENGTH_LONG).show();
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

    private void goToLoggedInMenuActivity() {
        Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
        startActivity(goToLoggedInMenuActivityIntent);
    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
        startActivity(goToMainActivityIntent);
    }
}