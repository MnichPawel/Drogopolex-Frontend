package com.example.drogopolex.activities;

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
import com.example.drogopolex.R;
import com.example.drogopolex.RequestSingleton;

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
    Button addEvent;
    EditText localizationInput;
    //EditText eventTypeInput; //Zastapione Spinnerem
    Spinner spinnerEvTyp;
    String wybranaAktywnosc;
    LocationManager locationManager;
    double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        listaZdarzen.add(0,"Wybierz typ"); //PAWEŁ NAWET TEGO NIE PRÓBUJ a tym bardziej Ty Kamil!!!
        goToLoggedInMenuActivity = (Button) findViewById(R.id.go_back_new_event);
        addEvent = (Button) findViewById(R.id.addNewEventButton);
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
            Toast.makeText(getApplicationContext(), "OciekurkarurkaacotozaKamilNapsuł", Toast.LENGTH_LONG).show();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        goToLoggedInMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoggedInMenuActivity();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "siekliklo1", Toast.LENGTH_LONG).show();
                addEventRequest();

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
    public static String[] getListOfEventTypesDep(){
        String[] lista = {"Wypadek","Korek","Patrol Policji","Roboty Drogowe"};
        return lista;
    }

    private void goToLoggedInMenuActivity() {
        Intent goToLoggedInMenuActivityIntent = new Intent(this, LoggedInMenuActivity.class);
        startActivity(goToLoggedInMenuActivityIntent);
    }

    private void goToMainActivity() {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    private void addEventRequest() {
        String localization = localizationInput.getText().toString();
        //String eventType = eventTypeInput.getText().toString();

        if (listaZdarzen.get(0).equals(wybranaAktywnosc) || "".equals(wybranaAktywnosc)) {
            Toast.makeText(getApplicationContext(), "Wybierz typ zdarzenia", Toast.LENGTH_LONG).show();

        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("localization", localization);
                jsonObject.put("longitude", longitude);
                jsonObject.put("latitude", latitude);
                jsonObject.put("type", wybranaAktywnosc);
                // jsonObject.put("type", eventType);

                String url = "http://10.0.2.2:5000/addEvent";
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean isSuccess = false;
                        String stringError = "";

                        try {
                            isSuccess = response.getBoolean("success");
                            if (!isSuccess) {
                                stringError = response.getString("errorString");
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
}