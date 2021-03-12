package com.example.drogopolex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    Button goToMainActivity;
    Button registerButton;
    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText passwordRepeatInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        else
        {
            Toast.makeText(this.getApplicationContext(),email,Toast.LENGTH_LONG).show();
        }



    }
    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }
}