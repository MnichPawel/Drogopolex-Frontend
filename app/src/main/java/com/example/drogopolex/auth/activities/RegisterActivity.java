package com.example.drogopolex.auth.activities;

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
import com.android.volley.toolbox.Volley;
import com.example.drogopolex.R;
import com.example.drogopolex.RequestSingleton;
import com.example.drogopolex.activities.main.LoggedInMenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    Button goToMainActivity;
    Button registerButton;
    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText passwordRepeatInput;
    RequestQueue rq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rq= Volley.newRequestQueue(this);

        goToMainActivity = (Button) findViewById(R.id.go_back_register);
        registerButton = (Button) findViewById(R.id.registerButton);
        nameInput = (EditText) findViewById(R.id.editTextTextPersonName);
        emailInput = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passwordInput = (EditText) findViewById(R.id.editTextTextPassword3);
        passwordRepeatInput = (EditText) findViewById(R.id.editTextTextPassword2);


        goToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // goToMainActivity();
                registerRequest();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(sp.getBoolean("loggedIn", false)){
            goToLoggedInMenuActivity();
        }
    }


    private void registerRequest() {

        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String pass1 = passwordInput.getText().toString();
        String pass2 = passwordRepeatInput.getText().toString();
        if(!pass1.equals(pass2)){
            Toast.makeText(this.getApplicationContext(),name,Toast.LENGTH_LONG).show();
            return;
        }

        try {

            //wrzucenie podanych danych do jsona
            JSONObject jo = new JSONObject();
            jo.put("name",name);
            jo.put("email",email);
            jo.put("password",pass1);

            String url= "http://10.0.2.2:5000/register";
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

                    //Jesli uda sie zarejestrowac, uzytkownik przenoszony jest do ekranu logowania
                    if(isSuccess){
                        Toast.makeText(getApplicationContext(),"Konto utworzone",Toast.LENGTH_LONG).show();
                        goToLoginActivity();
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
    private void goToLoginActivity() {
        Intent goToLoginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(goToLoginActivityIntent);
    }
    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
        startActivity(goToMainActivityIntent);
    }
    private void goToLoggedInMenuActivity() {
        Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
        startActivity(goToLoggedInMenuActivityIntent);
    }
}