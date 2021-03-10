package com.example.drogopolex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    }

    private void goToLoginActivity() {
        Intent goToLoginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(goToLoginActivityIntent);
    }

    private void goToRegisterActivity() {
        Intent goToRegisterActivityIntent = new Intent(this, RegisterActivity.class);
        startActivity(goToRegisterActivityIntent);
    }
}