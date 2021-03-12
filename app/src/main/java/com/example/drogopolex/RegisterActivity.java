package com.example.drogopolex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


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
    /*
        URL url = null;
        try {
            url = new URL("10.0.2.2:5000");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\"name\": 'Upendra', 'job': \"Programmer\"}";
            OutputStream os = con.getOutputStream();
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
*/

    }
    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }
}