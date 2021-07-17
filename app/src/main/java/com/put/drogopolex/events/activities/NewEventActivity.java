package com.put.drogopolex.events.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.put.drogopolex.R;
import com.put.drogopolex.RequestSingleton;
import com.put.drogopolex.auth.activities.LoggedInMenuActivity;
import com.put.drogopolex.auth.activities.LoginMenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class NewEventActivity extends AppCompatActivity implements LocationListener {

    ArrayList<String> listaZdarzen = NewEventActivity.getListOfEventTypes();


    Button goToLoggedInMenuActivity;
    Button addEvent, addEventGPS;
    EditText localizationInput;
    //EditText eventTypeInput; //Zastapione Spinnerem
    Spinner spinnerEvTyp;
    String wybranaAktywnosc;
    LocationManager locationManager;
    double latitude, longitude;

    private final int PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        listaZdarzen.add(0,"Wybierz typ"); //PAWEŁ NAWET TEGO NIE PRÓBUJ a tym bardziej Ty Kamil!!!
        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_new_event);
        addEvent = (Button) findViewById(R.id.addNewEventButton);
        addEventGPS = (Button) findViewById(R.id.addNewEventGPSButton);
        localizationInput = (EditText) findViewById(R.id.editTextLocalization);
        //eventTypeInput = (EditText) findViewById(R.id.editTextEventType);
        spinnerEvTyp= (Spinner) findViewById(R.id.spinnerEvent); //lista wyskakujaca
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaZdarzen);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerEvTyp.setAdapter(dataAdapter);
        spinnerEvTyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wybranaAktywnosc = listaZdarzen.get(i); //jak uzytkownik cos wybral to to ustaw
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                wybranaAktywnosc=""; //jak nie to nic nie ustawiaj
            }
        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSIONS_REQUEST_LOCATION);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);


        goToLoggedInMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventByInputRequest();
            }
        });

        addEventGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventByGPSRequest();
            }
        });



        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            goToMainActivity();
        }
    }
    public static ArrayList<String> getListOfEventTypes(){
        ArrayList<String> lista = new ArrayList<String>(Arrays.asList("Wypadek","Korek","Patrol Policji","Roboty Drogowe"));
        return lista;
    }

    private void goToLoggedInMenuActivity() {
        Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
        startActivity(goToLoggedInMenuActivityIntent);
    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
        startActivity(goToMainActivityIntent);
    }

    private void addEventByInputRequest() {
        String localization = localizationInput.getText().toString();
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        if (listaZdarzen.get(0).equals(wybranaAktywnosc) || "".equals(wybranaAktywnosc)) {
            Toast.makeText(getApplicationContext(), "Wybierz typ zdarzenia", Toast.LENGTH_LONG).show();

        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("gps", false);
                jsonObject.put("localization", localization);
                jsonObject.put("longitude", "");
                jsonObject.put("latitude", "");
                jsonObject.put("type", wybranaAktywnosc);
                jsonObject.put("user_id", user_id);
                jsonObject.put("token", token);

                String url = "http://10.0.2.2:5000/addEvent";
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean isSuccess = false;
                        String stringError = "";

                        try {
                            isSuccess = response.getBoolean("success");
                            if (!isSuccess) {
                                stringError = response.getString("error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (isSuccess) {
                            Toast.makeText(getApplicationContext(), "Zgłoszenie zostało dodane", Toast.LENGTH_LONG).show();
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
        }
    }

    private void addEventByGPSRequest() {
        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");
        String token = sp.getString("token", "");

        if (listaZdarzen.get(0).equals(wybranaAktywnosc) || "".equals(wybranaAktywnosc)) {
            Toast.makeText(getApplicationContext(), "Wybierz typ zdarzenia", Toast.LENGTH_LONG).show();

        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("gps", true);
                jsonObject.put("localization", "");
                jsonObject.put("longitude", longitude);
                jsonObject.put("latitude", latitude);
                jsonObject.put("type", wybranaAktywnosc);
                jsonObject.put("user_id", user_id);
                jsonObject.put("token", token);

                String url = "http://10.0.2.2:5000/addEvent";
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean isSuccess = false;
                        String stringError = "";

                        try {
                            isSuccess = response.getBoolean("success");
                            if (!isSuccess) {
                                stringError = response.getString("error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (isSuccess) {
                            Toast.makeText(getApplicationContext(), "Zgłoszenie zostało dodane", Toast.LENGTH_LONG).show();
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
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "PERMISSION GRANTED", Toast.LENGTH_LONG).show();
            }
        }
    }
}