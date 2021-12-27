package com.example.drogopolex.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import com.example.drogopolex.model.LocationDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

public class LocationLiveData extends LiveData<LocationDetails> {

    private static final long ONE_MINUTE = 60000;
    static LocationRequest mLocationRequest;
    Context context;
    FusedLocationProviderClient fusedLocationClient;

    public LocationLiveData(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(ONE_MINUTE);
            mLocationRequest.setFastestInterval(ONE_MINUTE / 4);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        startLocationUpdates();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        fusedLocationClient.removeLocationUpdates(new MyLocationCallback());
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, new MyLocationCallback(), null);
    }


    class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult == null) return;

            for (Location location : locationResult.getLocations()) {
                setLocationData(location);
            }
        }

        private void setLocationData(Location location) {
            if (location != null) {
                setValue(new LocationDetails(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude())));
            }
        }
    }
}
