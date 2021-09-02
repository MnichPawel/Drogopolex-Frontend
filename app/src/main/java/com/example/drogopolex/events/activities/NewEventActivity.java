package com.example.drogopolex.events.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.activities.LoggedInMenuActivity;
import com.example.drogopolex.auth.activities.LoginMenuActivity;
import com.example.drogopolex.constants.EventTypes;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.databinding.ActivityNewEventBinding;
import com.example.drogopolex.events.listeners.SpinnerHolder;
import com.example.drogopolex.events.utils.NewEventAction;
import com.example.drogopolex.events.viewModel.NewEventViewModel;
import com.example.drogopolex.listeners.BasicListener;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.example.drogopolex.model.LocationDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static com.example.drogopolex.constants.AppConstant.PERMISSIONS_REQUEST_LOCATION;

public class NewEventActivity extends AppCompatActivity implements BasicListener, SharedPreferencesHolder, SpinnerHolder {
    ActivityNewEventBinding activityNewEventBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_event);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, EventTypes.getEventTypes());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        activityNewEventBinding.setSpinnerAdapter(spinnerArrayAdapter);

        activityNewEventBinding.setViewModel(new NewEventViewModel(getApplication()));
        activityNewEventBinding.executePendingBindings();
        activityNewEventBinding.getViewModel().basicListener = this;
        activityNewEventBinding.getViewModel().spinnerHolder = this;
        activityNewEventBinding.getViewModel().sharedPreferencesHolder = this;

        activityNewEventBinding.getViewModel().getAction().observe(this, new Observer<NewEventAction>() {
            @Override
            public void onChanged(NewEventAction newEventAction) {
                if(newEventAction != null){
                    handleAction(newEventAction);
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }
    }

    private void handleAction(NewEventAction newEventAction) {
        switch (newEventAction.getValue()) {
            case NewEventAction.SHOW_LOGGED_IN:
                Intent goToLoggedInIntent = new Intent(this, LoggedInMenuActivity.class);
                startActivity(goToLoggedInIntent);
                break;
            case NewEventAction.ADD_EVENT_BY_GPS:
                prepRequestLocationUpdates();
                break;
        }
    }

    private void prepRequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSIONS_REQUEST_LOCATION);
        } else {
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        activityNewEventBinding.getViewModel().getLocationLiveData().observe(this, new Observer<LocationDetails>() {
            @Override
            public void onChanged(LocationDetails locationDetails) {
                activityNewEventBinding.getViewModel().onAddNewEventGpsReady(locationDetails);
            }
        });
    }

    @Override
    public void onSuccess(LiveData<BasicResponse> response) {
        response.observe(this, new Observer<BasicResponse>() {
            @Override
            public void onChanged(BasicResponse basicResponse) {
                if(basicResponse != null) {
                    if ("true".equals(basicResponse.getSuccess())) {
                        Toast.makeText(NewEventActivity.this, "Operacja powiodła się.", Toast.LENGTH_SHORT).show();
                        handleAction(new NewEventAction(NewEventAction.SHOW_LOGGED_IN));
                    } else {
                        String errorMessage = basicResponse.getError();
                        Toast.makeText(NewEventActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewEventActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getSelectedItem() {
        return (String) activityNewEventBinding.spinnerEvent.getSelectedItem();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }
}