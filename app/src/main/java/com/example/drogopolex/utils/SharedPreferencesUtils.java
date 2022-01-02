package com.example.drogopolex.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.drogopolex.constants.AppConstant;

public class SharedPreferencesUtils {

    private SharedPreferencesUtils() {
        Log.e("SharedPreferencesUtils", "Utility class.");
    }

    public static void resetSharedPreferences(SharedPreferences sp) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(AppConstant.TOKEN_SHARED_PREFERENCES, "");
        spEditor.putString(AppConstant.USER_ID_SHARED_PREFERENCES, "");
        spEditor.putBoolean("loggedIn", false);
        spEditor.apply();
    }
}
