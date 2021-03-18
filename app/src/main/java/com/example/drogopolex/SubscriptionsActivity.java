package com.example.drogopolex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SubscriptionsActivity extends AppCompatActivity {
    Button goToLoggedInMenuActivity;
    Button addSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_subscriptions);
        addSubscription = (Button) findViewById(R.id.addSubscription);
        Toast.makeText(getApplicationContext(),String.valueOf(addSubscription.getId()),Toast.LENGTH_LONG).show();

        goToLoggedInMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });

        addSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSubscribeActivity();
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

    private void goToSubscribeActivity() {
        Intent goToSubscribeActivityIntent = new Intent(this, SubscribeActivity.class);
        startActivity(goToSubscribeActivityIntent);
    }
}