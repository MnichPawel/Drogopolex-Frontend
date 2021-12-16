package com.example.drogopolex.auth.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.listeners.LoginListener;
import com.example.drogopolex.auth.utils.LoginAction;
import com.example.drogopolex.auth.viewModel.LoginViewModel;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.databinding.ActivityLoginBinding;
import com.example.drogopolex.events.activities.MapActivity;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;

public class LoginActivity extends AppCompatActivity implements LoginListener, MoPubInterstitial.InterstitialAdListener {
    public MoPubInterstitial interstitialAd;
    private String adIdMopub="24534e1901884e398f1253216226017e";
    private int LOCATION_PERMISSION_CODE=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setViewModel(new LoginViewModel());
        activityLoginBinding.executePendingBindings();
        activityLoginBinding.getViewModel().loginListener = this;

        //ads
        initialiseMopubSDK(adIdMopub);


        activityLoginBinding.getViewModel().getAction().observe(this, loginAction -> {
            if (loginAction != null) {
                handleAction(loginAction);
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if (sp.getBoolean("loggedIn", false)) {
            Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
            startActivity(goToMapActivityIntent);
        }
        checkIfUserPermitedLocation();

    }
    private void checkIfUserPermitedLocation(){
        //proszenie o zgode na udostepnienie lokalizacji
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            Toast.makeText(LoginActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Potrzebna zgoda")
                    .setMessage("Do działania aplikacji potrzebna jest Twoja zgoda na sprawdzanie Twojej lokalizacji")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private void handleAction(LoginAction loginAction) {

        switch (loginAction.getValue()) {
            case LoginAction.SHOW_MAP:
                checkIfUserPermitedLocation();
                Log.d("REK","HANDUL AKSZSZYN");
                if (interstitialAd.isReady()) { //show ad if ready
                    interstitialAd.show();
                    Log.d("REK","pokazaj wasc");
                } else {
                    goToMapActivity();
                }
                break;
            case LoginAction.SHOW_LOGIN_MENU:
                Intent goToLoginMenuActivityIntent = new Intent(this, LoginMenuActivity.class);
                startActivity(goToLoginMenuActivityIntent);
                break;
        }

    }
    private void goToMapActivity(){
        Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
        startActivity(goToMapActivityIntent);
    }
    @Override
    public void onSuccess(LiveData<LoginResponse> response) {
        response.observe(this, result -> {
            if (result != null) {
                if ("true".equals(result.getSuccess())) {
                    String userId = result.getUserId();
                    String token = result.getToken();

                    SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sp.edit();
                    spEditor.putString("token", token);
                    spEditor.putString("user_id", userId);
                    spEditor.putBoolean("loggedIn", true);
                    spEditor.apply();

                    handleAction(new LoginAction(LoginAction.SHOW_MAP));
                } else {
                    String errorMessage = result.getError();
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        Log.d("REK","zaladowana");
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
        Log.d("REK","fajlerd");
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {
        Log.d("REK","szolnienta");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {
        Log.d("REK","clkieknietea");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        Log.d("REK","zdmismisnowa");
        Toast.makeText(LoginActivity.this, "Dismisnieto reklame.", Toast.LENGTH_SHORT).show();
        // The interstitial has being dismissed. Resume / load state accordingly.
        goToMapActivity();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        interstitialAd.destroy();
        super.onDestroy();
    }

    private void initialiseMopubSDK(String idAd){
        Map<String, String> mediatedNetworkConfig1 = new HashMap<>();
        mediatedNetworkConfig1.put("<custom-adapter-class-data-key>","<custom-adapter-class-data-value>");
        Map<String, String> mediatedNetworkConfig2 = new HashMap<>();
        mediatedNetworkConfig2.put("<custom-adapter-class-data-key>","<custom-adapter-class-data-value>");

        SdkConfiguration sdkConfiguration =new SdkConfiguration.Builder(idAd)
                .withLegitimateInterestAllowed(false)
                .build();
        MoPub.initializeSdk(LoginActivity.this,sdkConfiguration,initialiseSdkListener());
    }
    private SdkInitializationListener initialiseSdkListener(){
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                interstitialAd = new MoPubInterstitial(LoginActivity.this, "24534e1901884e398f1253216226017e");
                interstitialAd.setInterstitialAdListener(LoginActivity.this);
                interstitialAd.load();
            }
        };
    }
}
