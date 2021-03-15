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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    Button goToMainActivity;
    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        goToMainActivity = (Button) findViewById(R.id.go_back_login);
        loginButton = (Button) findViewById(R.id.button);
        emailInput = (EditText) findViewById(R.id.editTextTextPersonName2);
        passwordInput = (EditText) findViewById(R.id.editTextTextPassword);
        goToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // goToMainActivity();
                loginRequest();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(sp.getBoolean("loggedIn", false)){
            goToLoggedInMenuActivity();
        }
    }

    private void loginRequest() {
        String email = emailInput.getText().toString();
        String pass = passwordInput.getText().toString();
        try {

            //wrzucenie podanych danych do jsona
            JSONObject jo = new JSONObject();
            jo.put("email",email);
            jo.put("password",pass);

            String url= "http://10.0.2.2:5000/login";
            JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, url, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    boolean isSuccess = false;
                    String stringError = "";
                    String token="";

                    try {
                        isSuccess = response.getBoolean("success");
                        stringError = response.getString("errorString");
                        token = response.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(isSuccess){
                        Toast.makeText(getApplicationContext(),"Zalogowano",Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor speditor = sp.edit();
                        speditor.putString("token",token);
                        speditor.putString("email",email);
                        speditor.putBoolean("loggedIn",true);
                        speditor.apply();

                        goToLoggedInMenuActivity();
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

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    private void goToLoggedInMenuActivity() {
        Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
        startActivity(goToLoggedInMenuActivityIntent);
    }
}