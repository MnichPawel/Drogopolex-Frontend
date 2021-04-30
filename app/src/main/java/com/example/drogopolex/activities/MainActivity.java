package com.example.drogopolex.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.drogopolex.R;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button goToLoginActivity;
    Button goToRegisterActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goToLoginActivity = (Button) findViewById(R.id.go_to_login_button);
        goToRegisterActivity = (Button) findViewById(R.id.go_to_register_button);

        goToLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginActivity();
            }
        });

        goToRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegisterActivity();
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(sp.getBoolean("loggedIn", false)){
            goToLoggedInMenuActivity();
        }
    }

    private void goToLoginActivity() {
        Intent goToLoginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(goToLoginActivityIntent);
    }

    private void goToRegisterActivity() {
        Intent goToRegisterActivityIntent = new Intent(this, RegisterActivity.class);
        startActivity(goToRegisterActivityIntent);
    }

    private void goToLoggedInMenuActivity() {
        Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
        startActivity(goToLoggedInMenuActivityIntent);
    }
}