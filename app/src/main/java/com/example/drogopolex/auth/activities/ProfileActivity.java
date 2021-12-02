package com.example.drogopolex.auth.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drogopolex.R;
import com.example.drogopolex.auth.utils.ProfileAction;
import com.example.drogopolex.auth.viewModel.ProfileViewModel;
import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.ProfileResponse;
import com.example.drogopolex.databinding.ActivityProfileBinding;
import com.example.drogopolex.events.activities.MapActivity;
import com.example.drogopolex.listeners.BasicListener;
import com.example.drogopolex.listeners.SharedPreferencesHolder;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class ProfileActivity extends AppCompatActivity implements SharedPreferencesHolder, BasicListener {

    //ads
    private String adIdMopub= "b195f8dd8ded45fe847ad89ed1d016da";
    MoPubView adBanner;
    MoPubView adBanner2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        activityProfileBinding.setViewModel(new ProfileViewModel());
        activityProfileBinding.executePendingBindings();
        activityProfileBinding.getViewModel().sharedPreferencesHolder = this;
        activityProfileBinding.getViewModel().basicListener = this;




        adBanner2 = (MoPubView) findViewById(R.id.profileAdBanner2);
        adBanner2.setAdUnitId(adIdMopub);
        adBanner2.loadAd();
        adBanner = (MoPubView) findViewById(R.id.profileAdBanner);
        adBanner.setAdUnitId(adIdMopub); // Enter your Ad Unit ID from www.mopub.com
        adBanner.loadAd();
        //initialiseMopubSDK(adIdMopub);

        activityProfileBinding.getViewModel().getAction().observe(this, new Observer<ProfileAction>() {
            @Override
            public void onChanged(ProfileAction profileAction) {
                if(profileAction != null){
                    handleAction(profileAction);
                }
            }
        });

        activityProfileBinding.getViewModel().getUserData().observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(ProfileResponse profileResponse) {
                if(profileResponse != null) {
                    if ("true".equals(profileResponse.getSuccess())) {
                        EditText username = findViewById(R.id.profile_change_username_textfield);
                        username.setText(profileResponse.getNazwa());
                        EditText email = findViewById(R.id.profile_change_email_textfield);
                        email.setText(profileResponse.getEmail());
                    } else {
                        Toast.makeText(ProfileActivity.this, profileResponse.getError(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
        if(!sp.getBoolean("loggedIn", false)){
            Intent goToMainActivityIntent = new Intent(this, LoginMenuActivity.class);
            startActivity(goToMainActivityIntent);
        }
    }

    private void handleAction(ProfileAction profileAction) {
        if (ProfileAction.SHOW_MAP == profileAction.getValue()) {
            Intent goToMapActivityIntent = new Intent(this, MapActivity.class);
            startActivity(goToMapActivityIntent);
        }
    }

    @Override
    public void onSuccess(LiveData<BasicResponse> response) {
        response.observe(this, result -> {
            if (result != null) {
                if ("true".equals(result.getSuccess())) {
                    Toast.makeText(ProfileActivity.this, "Operacja powiodła się.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Nie udało się przetworzyć odpowiedzi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("DrogopolexSettings", Context.MODE_PRIVATE);
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        adBanner.destroy();
        adBanner2.destroy();
    }


}